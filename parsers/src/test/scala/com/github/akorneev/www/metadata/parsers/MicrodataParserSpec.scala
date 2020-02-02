package com.github.akorneev.www.metadata.parsers

import java.io.InputStream

import com.github.akorneev.www.metadata.core.microdata.{Item, Property, StringValue, VocabId}
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
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Elizabeth")))),
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Daniel"))))
              ),
              Nil
            )
          )
        }
      }
      "002" in {
        withFile("microdata/002.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Elizabeth")))),
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Daniel"))))
              ),
              Nil
            )
          )
        }
      }
      "003" in {
        withFile("microdata/003.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Elizabeth"))))
              ),
              Nil
            )
          )
        }
      }
      "004" in {
        withFile("microdata/004.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("name") -> List(StringValue("Daniel"))))
              ),
              Nil
            )
          )
        }
      }
      "005" in {
        withFile("microdata/005.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(
                  Nil,
                  Set.empty,
                  None,
                  Map(
                    Property("name")        -> List(StringValue("Neil")),
                    Property("band")        -> List(StringValue("Four Parts Water")),
                    Property("nationality") -> List(StringValue("British"))
                  )
                )
              ),
              Nil
            )
          )
        }
      }
      "006" in {
        withFile("microdata/006.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("product-id") -> List(StringValue("9678AOU879"))))
              ),
              Nil
            )
          )
        }
      }
      "007" in {
        withFile("microdata/007.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("product-id") -> List(StringValue("9678AOU879"))))
              ),
              Nil
            )
          )
        }
      }
      "008" in {
        withFile("microdata/008.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, None, Map(Property("product-id") -> List(StringValue("This one rocks!"))))
              ),
              Nil
            )
          )
        }
      }
      "009" in {
        withFile("microdata/009.html") { html =>
          val items = MicrodataParser.parse(html, baseUri = Some("http://example.org/"))
          items should equal(
            (
              Set(
                Item(
                  Nil,
                  Set.empty,
                  Some(VocabId("https://schema.org/")),
                  Map(Property("https://schema.org/logo") -> List(StringValue("http://example.org/our-logo.png")))
                )
              ),
              Nil
            )
          )
        }
      }
      "010" in {
        withFile("microdata/010.html") { html =>
          val items = MicrodataParser.parse(html)
          items should equal(
            (
              Set(
                Item(Nil, Set.empty, Some(VocabId("https://schema.org/")), Map(Property("https://schema.org/name") -> List(StringValue("The Company"))))
              ),
              Nil
            )
          )
        }
      }
    }
  }
}
