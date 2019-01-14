package com.alanzplus.unblock.netease

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object Utility {
  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule);

  def toPrettyJson(obj: Any): String = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)

  def consoleWritePrettyJson(obj : Any) = println(toPrettyJson(obj))
}
