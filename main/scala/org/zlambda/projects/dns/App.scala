package org.zlambda.projects.dns

import java.io.{File, PrintWriter}
import java.util.concurrent.TimeUnit

import com.google.common.base.Stopwatch
import org.apache.logging.log4j.LogManager
import org.zlambda.projects.dns.chinaz.ChinaZ

import scala.sys.process._

object App {
  private val LOGGER = LogManager.getLogger(App.this.getClass)
  /**
    * TODO: instead of replace host file, implement DNS protocol
    */
  def replaceHost(lines: List[String]) = {
    {
      if (!new File("/etc/hosts-original").exists()) {
        "cp /etc/hosts /etc/hosts-original" !
      }
      "rm -f /etc/hosts" !
    }
    {
      val out = new PrintWriter(new File("/etc/hosts"))
      lines.foreach(out.println)
      out.close()
    }
    {
      s"cat /etc/hosts-original" #>> new File("/etc/hosts") !
    }
  }

  def main(args: Array[String]): Unit = {
    val proxy = Options.UNBLOCK_PROXY

    val dns = new ChinaZ(Options.DNS_LOOKUP_CONCURRENCY)
    val ping = new Ping(Options.PING_CONCURRENCY)
    val out = new PrintWriter(new File(Options.DNS_OUTPUT))
    try {
      out.println(s"music.163.com\t${proxy}")
      val st = Stopwatch.createStarted()

      val res = (
        (proxy, "music.163.com")
          +:
          args.toList
            .map((host) => (host, ping.getTime(dns.lookup(host))))
            .map((hostIpList) => {
              out.println((hostIpList._1 +: hostIpList._2.map(_._1)).mkString("\t"))
              if (hostIpList._2.isEmpty) {
                LOGGER.warn(s"No available IP for host ${hostIpList._1}, please try again")
                ("127.0.0.1", hostIpList._1)
              } else {
                (hostIpList._2.head._1, hostIpList._1)
              }
            })
        )
        .map((e) => List(e._1, e._2).mkString("\t"))

      if (Options.REPLACE_HOST) {
        replaceHost(res)
      }

      LOGGER.info(s"processed ${args.size} hosts took ${st.stop().elapsed(TimeUnit.SECONDS)} seconds")
    } catch {
      // TODO restore host file
      case e: Exception => {LOGGER.error(e)}
    } finally {
      out.close()
      ping.close()
      dns.close()
    }
  }
}
