package com.betfair.domain

import com.betfair.domain.PriceData.PriceData
import play.api.libs.json.Json

case class PriceProjection(priceData: List[PriceData])

object PriceProjection {
  implicit val formatPriceProjection = Json.format[PriceProjection]
}