package com.alanzplus.unblock.netease

import java.util.concurrent.{Callable, Executors, TimeUnit}

import org.apache.logging.log4j.LogManager

import scala.sys.process._

class Ping(concurrency: Int = 40) extends AutoCloseable {
  private[this] val logger = LogManager.getLogger(Ping.this.getClass)
  private[this] val executors = Executors.newFixedThreadPool(concurrency)

  def getFastestIpByAvgTime(ips: Seq[String]): (String, Double) = {
    ips.map(ip => {
      executors.submit(new Callable[(String, Double)] {
        override def call(): (String, Double) = {
          (ip, getPingAvgTime(ip))
        }
      })
    })
      .map(future => {
        try {
          Option(future.get(10, TimeUnit.SECONDS))
        } catch {
          case e: Throwable => {
            logger.error("Get fastest ip task failed", e)
            Option.empty
          }
        }
      })
      .filter(_.isDefined)
      .map(_.get)
      .minBy(_._2)
  }

  def getPingAvgTime(ip: String): Double = {
    try {
      logger.info(s"executing 'ping -c 5 $ip'")
      parsePingOutput(s"ping -c 5 $ip".lineStream.toList)._2
    } catch {
      case e: Throwable => {
        logger.error(s"Ping $ip threw exception.", e)
        Double.MaxValue
      }
    }
  }

  def parsePingOutput(output: Seq[String]): (Double, Double, Double) = {
    output.find(_.contains("min/avg/max"))
      .map {
        case STATICS_REGEXP(min, avg, max, _*) => (min.toDouble, avg.toDouble, max.toDouble)
        case _ => PING_ERROR_RESULT
      }
      .getOrElse(PING_ERROR_RESULT)
  }

  override def close(): Unit = {
    executors.shutdownNow()
  }

  private[this] val STATICS_REGEXP = raw".*=\s(.+)/(.+)/(.+)(/.*)ms".r
  private[this] val PING_ERROR_RESULT = (Double.MaxValue, Double.MaxValue, Double.MaxValue)
}
