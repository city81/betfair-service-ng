package com.betfair.domain

import play.api.libs.json.Json

case class PriceSize(price: Double, size: Double)

object PriceSize {
  implicit val readsPriceSize = Json.reads[PriceSize]
}