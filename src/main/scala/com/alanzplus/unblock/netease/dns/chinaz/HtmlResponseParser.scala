package com.alanzplus.unblock.netease.dns.chinaz

import java.util

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._

object HtmlResponseParser {
  private[this] val logger = LogManager.getLogger(HtmlResponseParser.this.getClass)

  def parseServers(html: String): List[Map[String, String]] = {
    null
  }

  /**
    * Parse to retrieve a map from server id to name.
    *
    * Example,
    *
    * {
    * "19048575": "西藏[电信]",
    * ...
    * }
    */
  def parseServerNames(rootDom: Document): Map[String, String] = {
    rootDom.select(serverIdNameListSelector).asScala.toList
      .filter(_.id != "")
      .map(e => (getId(e), getServerName(e)))
      .toMap
  }

  /**
    * Parse script element to retrieve a list of map which contains the server id and ip.
    *
    * Example,
    *
    * [
    * {"id": 19048575, "ip": "2xeIg7Rn7vKUx4bk7UV|EA==", "state": 0, "trytime": 0},
    * {"id": 19048576, "ip": "Rv90/Ksj1L5uT4T9vkFqHw==", "state": 0, "trytime": 0},
    * ...
    * ]
    */
  def parseServerIdIpList(rootDom: Document): List[Map[String, String]] = {

    val listJsonString = rootDom.select("script").asScala.toList
      .map(_.html())
      .filter(!_.isEmpty)
      .filter(_.contains("var servers = "))
      .map(e => e.substring(e.indexOf("["), e.indexOf(";")))
      .map(
        _
          .replaceAll("id", "\"id\"")
          .replaceAll("ip", "\"ip\"")
          .replaceAll("state", "\"state\"")
          .replaceAll("trytime", "\"trytime\""))

    if (listJsonString.isEmpty) {
      logger.warn("Cannot find server id-ip list")
      return List.empty
    }


    new ObjectMapper().readValue(listJsonString.head, new TypeReference[util.ArrayList[util.HashMap[String, String]]] {})
      .asInstanceOf[util.ArrayList[util.HashMap[String, String]]]
      .asScala
      .toList
      .map(_.asScala.toMap)
  }

  def parseAsDom(html: String) = {
    Jsoup.parse(html)
  }

  private val serverIdNameListSelector = "ul.DnsResuListWrap.fl.DnsWL li"
  private val getServerName = (e: Element) => e.child(0).text()
  private val getId = (e: Element) => e.id().substring(e.id.indexOf("_") + 1)
}
