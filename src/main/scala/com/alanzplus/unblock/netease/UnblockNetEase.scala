package com.alanzplus.unblock.netease

import java.io.{File, PrintWriter}

import com.alanzplus.unblock.netease.dns.chinaz.ChinaZ

object UnblockNetEase extends App {

  val domainNamesToLookup = args
  val music163DomainName = System.getProperty("music163DomainName", "music.163.com")
  val unblockYouKuIp = System.getProperty("unblockYouKuIp", "158.69.209.100")
  val dnsQueryConcurrency = Integer.getInteger("dns.query.concurrency", 10)
  val dnsQueryTimeoutInSeconds = Integer.getInteger("dns.query.timeoutInSeconds", 10)
  val pingConcurrency = Integer.getInteger("ping.concurrency", 40)
  val pingTimeoutInSeconds = Integer.getInteger("ping.timeoutInSeconds", 10)
  val outHostFile = System.getProperty("out.hostfile", "./out.hostfile")
  val out = new PrintWriter(new File(outHostFile))

  printArgsAndConfigs

  val dnsLookup = new ChinaZ(dnsQueryConcurrency)
  val ping = new Ping(pingConcurrency)
  try {
    val resolvedIps = resolveIps(domainNamesToLookup)
    println("Resolved Ips:\n")
    Utility.consoleWritePrettyJson(resolvedIps)
    writeToFile(resolvedIps)
  } finally {
    dnsLookup.close()
    ping.close()
    out.close()
  }

  private def writeToFile(resolvedIps: Map[String, Map[String, String]]) = {
    val hostFileFormat = toHostFileFormat(resolvedIps, (unblockYouKuIp, music163DomainName))
      .map(entry => s"${entry._1} ${entry._2}")
      .mkString("\n")
    println(s"Generated host file content:\n$hostFileFormat")
    out.println(hostFileFormat)
  }

  private def toHostFileFormat(resolvedIps: Map[String, Map[String, String]], music163Entry: (String, String)) = {
    music163Entry +:
      resolvedIps.toList.map(ele => {
        (
          ele._2("ip"),
          ele._1
        )
      })
  }

  private def resolveIps(hosts: Seq[String]) = {
    hosts
      .map(domainName => {
        (domainName, getFastestIp(domainName))
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2).head)
  }

  private def printArgsAndConfigs = {
    println("Use settings:\n")
    Utility.consoleWritePrettyJson(Map(
      "domainNamesToLookup" -> domainNamesToLookup,
      "music163DomainName" -> music163DomainName,
      "unblockYouKuIp" -> unblockYouKuIp,
      "dns.query.concurrency" -> dnsQueryConcurrency,
      "dns.query.timeoutInSeconds" -> dnsQueryTimeoutInSeconds,
      "ping.concurrency" -> pingConcurrency,
      "ping.timeoutInSeconds" -> pingTimeoutInSeconds,
      "out.hostfile" -> outHostFile
    ))
  }

  private def getFastestIp(domainName: String): Map[String, String] = {
    val ipWithAvgTime = ping.getFastestIpByAvgTime(dnsLookup.lookup(domainName))
    Map("ip" -> ipWithAvgTime._1, "avgTime" -> ipWithAvgTime._2.toString)
  }
}
