package com.github.akorneev.www.metadata.parsers

import java.io.InputStream
import java.net.URI

import com.github.akorneev.www.metadata.core.microdata._
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object MicrodataParser {
  sealed trait Error
  case class MicrodataError(elem: Element) extends Error
  case class IdNotFound(id: String)        extends Error

  type ParseResult = (Set[Item], List[Error])

  def parse(html: String): ParseResult = {
    val doc = Jsoup parse html
    parse(doc)
  }

  def parse(html: InputStream, charsetName: Option[String] = Some("UTF-8"), baseUri: Option[String] = None): ParseResult = {
    val doc = Jsoup.parse(html, charsetName.orNull, baseUri.getOrElse(""))
    parse(doc)
  }

  private def parse(doc: Document): ParseResult = {
    val itemScopes         = doc.select("[itemscope]").asScala.toSeq
    val topLevelItemScopes = itemScopes filterNot (_ hasAttr "itemprop")
    val items: Seq[(Item, List[Error])] = for (itemScope <- topLevelItemScopes) yield {
      val vocabId             = getVocabId(itemScope)
      val (propElems, errors) = getItemPropElems(itemScope, doc)
      val propList = propElems flatMap { elem =>
        val props = getProps(elem, vocabId)
        val value = getValue(elem)
        props map (p => (p, value)) groupBy (_._1) map { case (p, vs) => (p, vs.map(_._2).toList) }
      }
      (Item(types = Nil, ids = Set.empty, vocabId = vocabId, props = propList.toMap), errors)
    }
    items.foldLeft((Set.empty[Item], List.empty[Error])) { case ((items, errors), (i, es)) => (items + i, errors ++ es) }
  }

  private def getItemPropElems(item: Element, doc: Document): (List[Element], List[Error]) = {
    @tailrec def loop(
        memory: List[Element],
        pending: List[Element],
        results: List[Element],
        metadataErrors: List[Error]
    ): (List[Element], List[Error]) = pending match {
      case Nil => (sortInTreeOrder(results), metadataErrors.reverse)
      case current :: rest =>
        if (memory contains current) loop(memory, rest, results, MicrodataError(current) :: metadataErrors)
        else {
          val newPending = if (!current.hasAttr("itemscope")) current.children().asScala.toList else Nil
          val newResults = if (current.hasAttr("itemprop") && getProps(current, None).nonEmpty) List(current) else Nil
          loop(current :: memory, newPending ++ rest, newResults ++ results, metadataErrors)
        }
    }
    @tailrec def refsLoop(ids: List[String], elems: List[Element], errors: List[Error]): (List[Element], List[Error]) = ids match {
      case Nil => (elems, errors)
      case id :: rest =>
        Option(doc getElementById id) match {
          case Some(elem) => refsLoop(rest, elem :: elems, errors)
          case None       => refsLoop(rest, elems, IdNotFound(id) :: errors)
        }
    }
    val (refs, errors) = if (item.hasAttr("itemref")) refsLoop(splitOnSpaces(item attr "itemref"), Nil, Nil) else (Nil, Nil)
    val children       = item.children().asScala.toList
    loop(memory = List(item), pending = refs ++ children, results = Nil, metadataErrors = errors)
  }

  private def getProps(elem: Element, vocabId: Option[VocabId]): Set[Property] = {
    @tailrec def loop(tokens: List[String], props: List[Property]): List[Property] = tokens match {
      case Nil => props.reverse
      case tok :: rest =>
        if (isAbsoluteUrl(tok)) loop(rest, Property(tok) :: props)
        else
          vocabId match {
            case Some(VocabId(id)) => loop(rest, Property(id + tok) :: props)
            case None              => loop(rest, Property(tok) :: props)
          }
    }
    val tokens = splitOnSpaces(elem attr "itemprop")
    loop(tokens, props = Nil).toSet
  }

  private def getValue(elem: Element): Value =
    if (elem hasAttr "itemscope") ItemValue(getItem(elem))
    else if (elem hasAttr "content") StringValue(elem attr "content")
    else
      elem.tagName() match {
        case "audio" | "embed" | "iframe" | "img" | "source" | "track" | "video" =>
          if (elem hasAttr "src") StringValue(elem absUrl "src")
          else StringValue("")
        case "a" | "area" | "link" =>
          if (elem hasAttr "href") StringValue(elem absUrl "href")
          else StringValue("")
        case "object" =>
          if (elem hasAttr "data") StringValue(elem absUrl "data")
          else StringValue("")
        case "data" | "meter" if elem hasAttr "value" => StringValue(elem attr "value")
        case "time" if elem hasAttr "datetime"        => StringValue(elem attr "datetime")
        case _                                        => StringValue(elem.text())
      }

  private def getVocabId(elem: Element): Option[VocabId] =
    if (elem hasAttr "itemtype") {
      @tailrec def loop(tokens: List[String], potentialValues: List[String]): Option[VocabId] = tokens match {
        case Nil => potentialValues.reverse.headOption map VocabId
        case tok :: rest =>
          val id =
            if (tok contains '#') tok.substring(0, tok indexOf '#' + 1)
            else if (tok contains '/') {
              val last = tok lastIndexOf '/'
              print(s"last=$last, len=${tok.length}, tok=$tok")
              tok.substring(0, last + 1)
            } else tok + "/"
          loop(rest, id :: potentialValues)
      }
      val tokens = splitOnSpaces(elem attr "itemtype")
      loop(tokens, Nil)
    } else None

  private def getItem(elem: Element): Item = ???

  private def sortInTreeOrder(elems: Seq[Element]): List[Element] = {
    import Ordering.Implicits._
    def parents(e: Element): List[Element] = e.parents().asScala.toList.reverse
    implicit def ord: Ordering[Element] = Ordering.fromLessThan {
      case (e1, e2) if e1 == e2 => false
      case (e1, e2) if e1.parent() == e2.parent() =>
        val children = e1.parent().children()
        children.indexOf(e1) < children.indexOf(e2)
      case (e1, e2) => implicitly[Ordering[List[Element]]].lt(parents(e1), parents(e2))
    }
    elems.sortBy(parents).toList
  }

  private def splitOnSpaces(s: String): List[String] = s.split("[\\u0020\\u0009\\u000A\\u000C\\u000D]+").filterNot(_.isEmpty).toList

  private def isAbsoluteUrl(s: String): Boolean = new URI(s).isAbsolute
}
