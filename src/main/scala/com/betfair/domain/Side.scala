package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object Side extends Enumeration {
  type Side = Value
  val BACK, LAY = Value

  implicit val enumReads: Reads[Side] = EnumUtils.enumReads(Side)

  implicit def enumWrites: Writes[Side] = EnumUtils.enumWrites
}
