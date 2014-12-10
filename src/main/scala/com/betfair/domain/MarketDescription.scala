package com.betfair.domain

import org.joda.time.DateTime
import play.api.libs.json.Json

case class MarketDescription(persistenceEnabled: Boolean,
                        bspMarket: Boolean,
                        marketTime: DateTime,
                        suspendTime: DateTime,
                        settleTime: DateTime,
                        bettingType: String,
                        turnInPlayEnabled: Boolean,
                        marketType: String,
                        regulator: String,
                        marketBaseRate: Double,
                        discountAllowed: Boolean,
                        wallet: String,
                        rules: String,
                        rulesHasDate: Boolean,
                        clarifications: String)

object MarketDescription {
  implicit val readsMarketDescription = Json.format[MarketDescription]
}