package com.github.akorneev.www.metadata.parsers

import java.io.InputStream

import com.github.akorneev.www.metadata.core.microdata.Item
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object MicrodataParser {

  def parse(html: String): Set[Item] = {
    val doc = Jsoup parse html
    parse(doc)
  }

  def parse(html: InputStream, charsetName: Option[String] = Some("UTF-8"), baseUri: Option[String] = None): Set[Item] = {
    val doc = Jsoup.parse(html, charsetName.orNull, baseUri.getOrElse(""))
    parse(doc)
  }

  private def parse(doc: Document): Set[Item] = {
    val itemScopes: Elements = doc.select("[itemscope]")
    ???
  }
}
