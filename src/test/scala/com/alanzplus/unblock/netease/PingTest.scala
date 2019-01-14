package com.alanzplus.unblock.netease

import org.junit.runner.RunWith
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PingTest extends FunSuite with Matchers {
  private[this] val PING_ERROR_RESULT = (Double.MaxValue, Double.MaxValue, Double.MaxValue)

  test("[HappyCase] Parse Mac Ping output") {
    val output = Seq("a", "b", "round-trip min/avg/max/stddev = 274.755/353.224/499.989/86.687 ms")

    new Ping().parsePingOutput(output) shouldBe (274.755, 353.224, 499.989)
  }

  test("[UnHappyCase 1] Parse Mac Ping output") {
    val stream = Seq("a", "b")

    new Ping().parsePingOutput(stream) shouldBe PING_ERROR_RESULT
  }

  test("[UnHappyCase 2 Parse Mac Ping output") {
    val stream = Seq("a", "b", "min/avg/max ")

    new Ping().parsePingOutput(stream) shouldBe PING_ERROR_RESULT
  }
}
