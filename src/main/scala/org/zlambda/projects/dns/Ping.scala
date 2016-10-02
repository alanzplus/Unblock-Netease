package org.zlambda.projects.dns

import java.util.concurrent.{Callable, Executors, TimeUnit}

import org.apache.logging.log4j.LogManager

import scala.concurrent.TimeoutException
import scala.sys.process._

class Ping(concurrency: Int = 32) extends AutoCloseable {
  private val LOGGER = LogManager.getLogger(Ping.this.getClass)
  private val service = Executors.newFixedThreadPool(concurrency)

  def getTime(hosts: List[String]): List[(String, Double)] = {
    hosts
      .map((host) => service.submit(new Callable[Option[(String, Double)]] {
        override def call(): Option[(String, Double)] = {
          LOGGER.info(s"ping $host")
          try {
            Option(
              (
                host,
                s"ping -c 5 ${host}".lineStream
                  .filter(_.contains("rtt min/avg/max/mdev"))
                  .map(_.split("/")(4))
                  .toList
                  .head
                  .toDouble
                )
            )
          } catch {
            case e: Exception => {
              LOGGER.error(s"Unreachable ip ${host}")
              Option.empty
            }
          }
        }
      }))
      .map((future) => {
        try {
          future.get(5, TimeUnit.SECONDS)
        } catch {
          case e: TimeoutException => {
            LOGGER.error(s"Ping timeout, so discard this ip")
            Option.empty
          }
          case e: Exception => {
            LOGGER.error(s"Ping error ${e.getMessage}", e)
            Option.empty
          }
        }
      })
      .filter(_.isDefined)
      .map(_.get)
      .sortBy(_._2)
  }

  override def close(): Unit = {
    service.shutdownNow()
  }
}


