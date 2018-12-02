package com.alanzplus.unblock.netease.dns

/**
  * Retrieve a list of DNS addresses given a host domain name
  */
trait DnsLookup extends AutoCloseable {
  def lookup(domainName: String): List[String]
}
