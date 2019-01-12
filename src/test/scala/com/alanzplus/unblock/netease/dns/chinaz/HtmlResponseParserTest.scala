package com.alanzplus.unblock.netease.dns.chinaz

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class HtmlResponseParserTest extends FunSuite with Matchers {
  test("Parse Id Service Name Map") {
    val source = Source.fromResource("chinaz-lookup-response.html").mkString

    val idServiceNameMap = HtmlResponseParser.parseServerNames(HtmlResponseParser.parseAsDom(source))

    idServiceNameMap.size shouldBe 2
    idServiceNameMap should contain("19048575" -> "西藏[电信]")
    idServiceNameMap should contain("19048576" -> "山东[联通]")
  }

  test("Parse server script") {
    val source = Source.fromResource("chinaz-lookup-response.html").mkString

    val list = HtmlResponseParser.parseServerIdIpList(HtmlResponseParser.parseAsDom(source))

    list.size shouldBe 3
    list should contain only(
      Map("id" -> "19048575", "ip" -> "2xeIg7Rn7vKUx4bk7UV|EA==", "state" -> "0", "trytime" -> "0"),
      Map("id" -> "19048576", "ip" -> "Rv90/Ksj1L5uT4T9vkFqHw==", "state" -> "0", "trytime" -> "0"),
      Map("id" -> "19048578", "ip" -> "UsXmWPyUMCAl22fytxVeYA==", "state" -> "0", "trytime" -> "0"))
  }

  test("get Dns Servers") {
    val source = Source.fromResource("chinaz-lookup-response.html").mkString

    val list = HtmlResponseParser.getDnsServers(source)

    println(list)

    list.size shouldBe 2
    list should contain only(
      Map("name" -> "西藏[电信]", "id" -> "19048575", "ip" -> "2xeIg7Rn7vKUx4bk7UV|EA==", "state" -> "0", "trytime" -> "0"),
      Map("name" -> "山东[联通]", "id" -> "19048576", "ip" -> "Rv90/Ksj1L5uT4T9vkFqHw==", "state" -> "0", "trytime" -> "0"))
  }
}
