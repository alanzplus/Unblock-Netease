package org.zlambda.projects.dns

trait DNSLookup {
  def lookup(host: String): List[String]
}
