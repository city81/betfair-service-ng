package com.betfair.domain

import org.joda.time.DateTime
import play.api.libs.json.Json

case class MarketDescription(persistenceEnabled: Boolean,
                        bspMarket: Boolean,
                        marketTime: Option[DateTime] = None,
                        suspendTime: Option[DateTime] = None,
                        settleTime: Option[DateTime] = None,
                        bettingType: String,
                        turnInPlayEnabled: Boolean,
                        marketType: String,
                        regulator: String,
                        marketBaseRate: Double,
                        discountAllowed: Boolean,
                        wallet: String,
                        rules: String,
                        rulesHasDate: Boolean,
                        clarifications: Option[String] = None)

object MarketDescription {

  import play.api.libs.json._

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)

  implicit val readsMarketDescription = Json.format[MarketDescription]
}