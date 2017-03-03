package com.betfair.domain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

case class MarketCatalogue(marketId: String,
                           marketName: String,
                           marketStartTime: Option[DateTime],
                           description: Option[MarketDescription] = None,
                           totalMatched: Double,
                           runners: Option[List[RunnerCatalog]],
                           eventType: Option[EventType],
                           competition: Option[Competition] = None,
                           event: Event)

object MarketCatalogue {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsMarketCatalogue = Json.reads[MarketCatalogue]
}