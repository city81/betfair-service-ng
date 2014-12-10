package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object MarketSort extends Enumeration {
  type MarketSort = Value
  val MINIMUM_TRADED, MAXIMUM_TRADED, MINIMUM_AVAILABLE, MAXIMUM_AVAILABLE, FIRST_TO_START, LAST_TO_START = Value

  implicit val enumReads: Reads[MarketSort] = EnumUtils.enumReads(MarketSort)

  implicit def enumWrites: Writes[MarketSort] = EnumUtils.enumWrites
}