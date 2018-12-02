package com.alanzplus.unblock.netease.dns.chinaz

import org.junit.runner.RunWith
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class HtmlResponseParserTest extends FunSuite with Matchers {
  test("Parse Id Service Name Map") {
    val source = Source.fromResource("chinaz-lookup-response.html").mkString

    val idServiceNameMap = new HtmlResponseParser(source).getIdServiceNameMap(source)

    idServiceNameMap.size shouldBe 2
    idServiceNameMap should contain ("19048575" -> "西藏[电信]")
    idServiceNameMap should contain ("19048576" -> "山东[联通]")
  }
}
