package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object MarketProjection extends Enumeration {
  type MarketProjection = Value
  val COMPETITION, EVENT, EVENT_TYPE, MARKET_START_TIME, MARKET_DESCRIPTION, RUNNER_DESCRIPTION, RUNNER_METADATA = Value

  implicit val enumReads: Reads[MarketProjection] = EnumUtils.enumReads(MarketProjection)

  implicit def enumWrites: Writes[MarketProjection] = EnumUtils.enumWrites
}