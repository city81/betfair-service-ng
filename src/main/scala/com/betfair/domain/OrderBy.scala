package com.betfair.domain

import play.api.libs.json.{Reads, Writes}

object OrderBy extends Enumeration {
  type OrderBy = Value
  val BY_MARKET, BY_MATCH_TIME, BY_PLACE_TIME, BY_SETTLED_TIME, BY_VOID_TIME = Value

  implicit val enumReads: Reads[OrderBy] = EnumUtils.enumReads(OrderBy)

  implicit def enumWrites: Writes[OrderBy] = EnumUtils.enumWrites
}
