package com.github.akorneev.www.metadata.parsers

import java.io.InputStream

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class MicrodataParserSpec extends AnyFreeSpec with Matchers {
  private def withFile[A](path: String)(f: InputStream => A): A = {
    val is = getClass.getResourceAsStream(path)
    try f(is)
    finally is.close()
  }

  "MicrodataParser" - {
    "should parse specification examples" - {
      "001" in {
        withFile("microdata/001.html") { html =>
          val items = MicrodataParser.parse(html)
          items.size should equal(2)
        }
      }
    }
  }
}
