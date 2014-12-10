package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object PriceData extends Enumeration {
  type PriceData = Value
  val SP_AVAILABLE, SP_TRADED, EX_BEST_OFFERS, EX_ALL_OFFERS, EX_TRADED = Value

  implicit val enumReads: Reads[PriceData] = EnumUtils.enumReads(PriceData)

  implicit def enumWrites: Writes[PriceData] = EnumUtils.enumWrites
}