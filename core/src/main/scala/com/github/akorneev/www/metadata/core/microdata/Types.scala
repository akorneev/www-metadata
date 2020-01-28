package com.github.akorneev.www.metadata.core.microdata

import java.net.URL

case class ItemType()

case class VocabId(value: String)

case class Property(name: String)

sealed trait Value
case class StringValue(value: String) extends Value
case class ItemValue(item: Item)      extends Value

case class Item(types: List[ItemType], ids: Set[URL], vocabId: Option[VocabId], props: Map[Property, List[Value]])
