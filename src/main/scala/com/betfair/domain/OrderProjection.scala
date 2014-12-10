package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object OrderProjection extends Enumeration {
  type OrderProjection = Value
  val ALL, EXECUTABLE, EXECUTION_COMPLETE = Value

  implicit val enumReads: Reads[OrderProjection] = EnumUtils.enumReads(OrderProjection)

  implicit def enumWrites: Writes[OrderProjection] = EnumUtils.enumWrites
}
