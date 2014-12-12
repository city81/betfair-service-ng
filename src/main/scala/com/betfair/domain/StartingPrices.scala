package com.betfair.domain

import play.api.libs.json.Json

case class StartingPrices(nearPrice: Double, farPrice: Double, backStakeTaken: Set[PriceSize],
                          layLiabilityTaken: Set[PriceSize], actualSP: Double)

object StartingPrices {
  implicit val readsStartingPrices = Json.reads[StartingPrices]
}