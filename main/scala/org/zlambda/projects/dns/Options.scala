package org.zlambda.projects.dns

object Options {
  val UNBLOCK_PROXY = System.getProperty("unblock", "45.32.72.192")
  val REPLACE_HOST = System.getProperty("replaceHost", "false").toBoolean
  val DNS_LOOKUP_TIMEOUT = System.getProperty("dns.timeout", "10").toInt
  val DNS_LOOKUP_RETRY = System.getProperty("dns.retry", "1").toInt
  val DNS_LOOKUP_CONCURRENCY = System.getProperty("dns.concurrency", "5").toInt
  val DNS_OUTPUT = System.getProperty("dns.output", "dns.output")
  val PING_TIMEOUT = System.getProperty("ping.timeout",  "5").toInt
  val PING_COUNT = System.getProperty("ping.count", "3").toInt
  val PING_RETRY = System.getProperty("ping.retry", "1").toInt
  val PING_CONCURRENCY = System.getProperty("ping.concurrency", "16").toInt
}
