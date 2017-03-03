package com.betfair.domain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

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

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsMarketDescription = Json.format[MarketDescription]
}