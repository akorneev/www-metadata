package com.github.akorneev.www.metadata.core.microdata

import java.net.URL

case class ItemType()

case class Property()

sealed trait Value
case class StringValue() extends Value
case class ItemValue()   extends Value

case class Item(types: List[ItemType], ids: Set[URL], props: Map[Property, Seq[Value]])
