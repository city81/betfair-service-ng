package com.betfair.domain

import com.betfair.domain.PriceData.PriceData
import play.api.libs.json.Json

case class PriceProjection(priceData: Set[PriceData])

object PriceProjection {
  implicit val writesPriceProjection = Json.writes[PriceProjection]
}