package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object MatchProjection extends Enumeration {
  type MatchProjection = Value
  val NO_ROLLUP, ROLLED_UP_BY_PRICE, ROLLED_UP_BY_AVG_PRICE = Value

  implicit val enumReads: Reads[MatchProjection] = EnumUtils.enumReads(MatchProjection)

  implicit def enumWrites: Writes[MatchProjection] = EnumUtils.enumWrites
}