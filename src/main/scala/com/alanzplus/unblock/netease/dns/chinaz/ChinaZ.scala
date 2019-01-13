package com.alanzplus.unblock.netease.dns.chinaz

import java.util
import java.util.concurrent.{Callable, Executors, TimeUnit}

import com.alanzplus.unblock.netease.Utility
import com.alanzplus.unblock.netease.dns.DnsLookup
import com.fasterxml.jackson.core.`type`.TypeReference
import okhttp3._
import org.apache.logging.log4j.LogManager

import scala.collection.JavaConverters._

class ChinaZ(concurrency: Int = 5) extends DnsLookup {
  private[this] val logger = LogManager.getLogger(ChinaZ.this.getClass)
  private[this] val httpClient = new OkHttpClient
  private[this] val executors = Executors.newFixedThreadPool(concurrency)

  override def lookup(domainName: String): List[String] = {
    val url = s"http://tool.chinaz.com/dns/?type=1&host=$domainName&ip="
    logger.info(s"Requesting ${url}")
    val response = httpClient.newCall(new Request.Builder().url(url).build()).execute()
    if (!response.isSuccessful) {
      logger.error(s"Unexpected status code, details $response")
      return List.empty
    }

    val dnsServers = HtmlResponseParser.getDnsServers(response.body.string)
    logger.info(s"DNS Servers $dnsServers")

    dnsServers.zipWithIndex.map {
      case (dnsServer, idx) => {
        new GetIpTask(dnsServer, domainName, idx, dnsServers.size)
      }
    }
      .map(executors.submit(_))
      .flatMap(future => {
        var res: List[String] = List.empty
        try {
          res = future.get(10, TimeUnit.SECONDS)
        } catch {
          case e: Exception => logger.error(s"Get DNS server response error ${e.getMessage}", e)
        }
        res.filter(!_.equals("127.0.0.1"))
      })
  }

  override def close(): Unit = {
    executors.shutdownNow();
  }

  /**
    * Query DNS Server
    *
    * = Request =
    *
    * POST http://tool.chinaz.com/AjaxSeo.aspx?t=dns&server=${dnsServer("ip")}&id=${dnsServer("id")}
    *
    * with form
    *
    * host: $lookupHost
    * type: 1
    * total: $dnsServer.length
    * process: $current
    * right: current
    *
    * = Response =
    * {
    * "state":1,
    * "id":19951711,
    * "list":[
    * {
    * "type":"A",
    * "result":"58.20.141.136",
    * "ipaddress":"湖南省岳阳市 联通",
    * "ttl":"521"
    * },
    * {
    * "type":"A",
    * "result":"61.163.111.203",
    * "ipaddress":"河南省新乡市 联通",
    * "ttl":"521"
    * },
    * {
    * "type":"A",
    * "result":"110.53.75.73",
    * "ipaddress":"湖南省衡阳市 网宿科技股份有限公司联通CDN节点",
    * "ttl":"521"
    * }
    * ]
    * }
    */
  class GetIpTask(dnsServer: Map[String, String], lookupHost: String, idx: Int, total: Int) extends Callable[List[String]] {
    override def call(): List[String] = {

      val response = queryDnsServer
      if (!response.isSuccessful) {
        logger.error(s"Unexpected status code, details $response")
        return List.empty
      }

      getAsScalaMap(jsonifyResponse(response.body.string))
        .get("list")
        .map(toScalaListOfMap)
        .getOrElse(List.empty)
        .map(_ ("result"))
    }

    private def toScalaListOfMap(alist: Any) = {
      alist.asInstanceOf[util.ArrayList[util.HashMap[String, String]]].asScala.toList.map(_.asScala.toMap[String, String])
    }

    private def getAsScalaMap(response: String) = {
      Utility.objectMapper.readValue(response, new TypeReference[util.HashMap[String, Object]]() {})
        .asInstanceOf[util.HashMap[String, Object]]
        .asScala
        .toMap
    }

    private def jsonifyResponse(responseBody: String) = {
      val trimResponse = responseBody.substring(1, responseBody.size - 1) // remove beginning "(" and ending ")"
      if (!trimResponse.contains("\"state\"")) {
        trimResponse
          .replaceAll("state", "\"state\"")
          .replaceAll("list", "\"list\"")
          .replaceAll("id", "\"id\"")
          .replaceAll("type", "\"type\"")
          .replaceAll("ipaddress", "\"ipaddress\"")
          .replaceAll("result", "\"result\"")
          .replaceAll("ttl", "\"ttl\"")
      } else {
        trimResponse
      }
    }

    private def queryDnsServer = {
      val url = s"http://tool.chinaz.com/AjaxSeo.aspx?t=dns&server=${dnsServer("ip")}&id=${dnsServer("id")}"
      logger.info(s"Submit DNS query ${url}.")
      httpClient.newCall(new Request.Builder()
        .url(url)
        .post(newForm)
        .build()).execute()
    }

    private def newForm = new FormBody.Builder()
      .add("host", lookupHost)
      .add("type", "1")
      .add("total", total.toString)
      .add("process", idx.toString)
      .add("right", idx.toString)
      .build()
  }

}

