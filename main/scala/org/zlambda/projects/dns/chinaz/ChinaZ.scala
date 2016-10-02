package org.zlambda.projects.dns.chinaz

import java.util
import java.util.concurrent.{Callable, Executors, TimeUnit}

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3._
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.zlambda.projects.dns.{DNSLookup, Options}

import scala.collection.JavaConversions._

class ChinaZ(client: OkHttpClient, concurrency: Int = 5) extends DNSLookup with AutoCloseable {
  private val service = Executors.newFixedThreadPool(concurrency)
  private val LOGGER = LogManager.getLogger(ChinaZ.this.getClass)

  def this(concurrency: Int) = this(new OkHttpClient, concurrency)

  override def lookup(host: String): List[String] = {
    val url = s"http://tool.chinaz.com/dns/?type=1&host=$host&ip="
    LOGGER.info(s"requesting ${url}")
    val res = client.newCall(new Request.Builder().url(url).build()).execute()
    if (!res.isSuccessful) {
      LOGGER.error(s"Unexpected status code, details $res")
      return List.empty
    }

    val dnsServers = parseServers(res.body().string(), host)

    dnsServers.map((server) => {
      service.submit(new Callable[List[String]] {
        override def call(): List[String] = {
          val mType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8")
          val form = new FormBody.Builder()
            .add("host", host)
            .add("type", "1")
            .add("total", dnsServers.size.toString)
            .add("process", "0")
            .add("right", "5")
            .build()

          val url = s"http://tool.chinaz.com/AjaxSeo.aspx?t=dns&server=${server("ip")}&id=${server("id")}"

          val request = new Request.Builder()
            .url(url)
            .post(form)
            .build()

          LOGGER.info(s"processing ${url}")

          val res = client.newCall(request).execute()
          if (!res.isSuccessful) {
            LOGGER.error(s"Unexpected status code, details $res")
          }

          var body = res.body().string()

          if (!body.contains("\"state\"")) {
            body = body
              .replaceAll("state", "\"state\"")
              .replaceAll("list", "\"list\"")
              .replaceAll("id", "\"id\"")
              .replaceAll("type", "\"type\"")
              .replaceAll("ipaddress", "\"ipaddress\"")
              .replaceAll("result", "\"result\"")
              .replaceAll("ttl", "\"ttl\"")
          }

          LOGGER.info(body)

          new ObjectMapper()
            .readValue(body.substring(1, body.length - 1), new TypeReference[util.HashMap[String, Object]]() {})
            .asInstanceOf[util.HashMap[String, Object]]
            .toMap
            .get("list")
            .map(_.asInstanceOf[util.ArrayList[util.LinkedHashMap[String, String]]].toList)
            .map(_.map(_.toMap[String, String]))
            .getOrElse(List.empty)
            .map(_ ("result"))
        }
      })
    })
    .flatMap((future) => {
      var res: List[String] = List.empty
      try {
        res = future.get(Options.DNS_LOOKUP_TIMEOUT, TimeUnit.SECONDS)
      } catch {
        case e: Exception => LOGGER.error(s"Get DNS server response error ${e.getMessage}", e)
      }
      res.filter(!_.equals("127.0.0.1"))
    })
  }


  override def close(): Unit = {
    service.shutdownNow()
  }

  def parseServers(html: String, host: String): List[Map[String, String]] = {
    val doc = Jsoup.parse(html);

    val serverNameMap = doc.select("ul.DnsResuListWrap.fl.DnsWL li").toList
      .filter(_.id != "")
      .map((e) => (e.id.substring(e.id.indexOf("_") + 1), e.child(0).text()))
      .toMap

    if (serverNameMap.isEmpty) {
      LOGGER.warn(s"Cannot find server name map given host $host")
      return List.empty
    }

    LOGGER.debug(s"server name map: $serverNameMap")

    val listJsonStr = doc.select("script").toList
      .map(_.html())
      .flatMap(_.split("\n"))
      .filter(!_.isEmpty)
      .filter(_.contains("var servers = "))
      .map((e) => e.substring(e.indexOf("["), e.indexOf(";")))
      .map(_.replaceAll("id", "\"id\"")
        .replaceAll("ip", "\"ip\"")
        .replaceAll("state", "\"state\"")
        .replaceAll("trytime", "\"trytime\""))

    if (listJsonStr.isEmpty) {
      LOGGER.warn(s"Cannot find server id given host $host")
      return List.empty
    }

    new ObjectMapper().readValue(listJsonStr.head, new TypeReference[util.ArrayList[util.HashMap[String, String]]]() {})
      .asInstanceOf[util.ArrayList[util.HashMap[String, String]]]
      .toList
      .map(_.toMap)
      .filter(_.get("id").exists(serverNameMap.contains))
      .map((e) => (e.toList :+ ("name", serverNameMap.get(e.get("id").get).get)).toMap)
  }
}

