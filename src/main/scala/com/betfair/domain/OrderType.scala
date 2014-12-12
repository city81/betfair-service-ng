package com.betfair.domain

import play.api.libs.json.{Writes, Reads, Json}

object OrderType extends Enumeration {
  type OrderType = Value
  val LIMIT, LIMIT_ON_CLOSE, MARKET_ON_CLOSE = Value

  implicit val enumReads: Reads[OrderType] = EnumUtils.enumReads(OrderType)

  implicit def enumWrites: Writes[OrderType] = EnumUtils.enumWrites
}
