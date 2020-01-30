package com.github.akorneev.www.metadata.parsers

import java.io.InputStream

import com.github.akorneev.www.metadata.core.microdata.{Item, Property, StringValue}
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
          items should equal(
            Set(
              Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Elizabeth")))),
              Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Daniel"))))
            )
          )
        }
      }
      "002" in {
        withFile("microdata/002.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            Set(
              Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Elizabeth")))),
              Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Daniel"))))
            )
          )
        }
      }
    }
  }
}
