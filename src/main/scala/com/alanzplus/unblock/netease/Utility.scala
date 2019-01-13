package com.alanzplus.unblock.netease

import com.fasterxml.jackson.databind.ObjectMapper

object Utility {
  val objectMapper = new ObjectMapper()

  def toPrettyJson(obj: Any): String = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
}
