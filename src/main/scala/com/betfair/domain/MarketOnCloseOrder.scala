package com.betfair.domain

import play.api.libs.json.Json

case class MarketOnCloseOrder(liability: Double)

object MarketOnCloseOrder {
  implicit val formatMarketOnCloseOrder = Json.format[MarketOnCloseOrder]
}