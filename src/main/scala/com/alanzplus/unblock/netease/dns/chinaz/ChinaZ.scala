package com.alanzplus.unblock.netease.dns.chinaz

import com.alanzplus.unblock.netease.dns.DnsLookup
import okhttp3.{OkHttpClient, Request}
import org.apache.logging.log4j.LogManager

class ChinaZ extends DnsLookup {
  private[this] val logger = LogManager.getLogger(ChinaZ.this.getClass)
  private[this] val httpClient = new OkHttpClient

  override def lookup(domainName: String): List[String] = {
    val url = s"http://tool.chinaz.com/dns/?type=1&host=$domainName&ip="
    logger.info(s"Requesting ${url}")
    val response = httpClient.newCall(new Request.Builder().url(url).build()).execute()
    if (!response.isSuccessful) {
      logger.error(s"Unexpected status code, details $response")
      return List.empty
    }
    logger.info(s"content ${response.body().string()}")
    null
  }

  override def close(): Unit = {

  }
}

