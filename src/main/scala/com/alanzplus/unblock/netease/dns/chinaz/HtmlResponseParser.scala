package com.alanzplus.unblock.netease.dns.chinaz

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

class HtmlResponseParser(val reponse: String) {

  def getIdServiceNameMap(html: String): Map[String, String] = {
    Jsoup.parse(html).select(serverIdNameListSelector).asScala.toList
      .filter(_.id != "")
      .map(e => (getId(e), getServerName(e)))
      .toMap
  }

  private val serverIdNameListSelector = "ul.DnsResuListWrap.fl.DnsWL li"
  private val getServerName = (e: Element) => e.child(0).text()
  private val getId = (e: Element) => e.id().substring(e.id.indexOf("_") + 1)
}
