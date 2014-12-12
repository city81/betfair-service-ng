package com.betfair.domain

import play.api.libs.json.Json

case class ExchangePrices(availableToBack: Set[PriceSize], availableToLay: Set[PriceSize], tradedVolume: Set[PriceSize])

object ExchangePrices {
  implicit val readsExchangePrices = Json.reads[ExchangePrices]
}