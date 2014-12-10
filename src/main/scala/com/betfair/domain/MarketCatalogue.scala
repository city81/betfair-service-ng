package com.betfair.domain

import org.joda.time.DateTime
import play.api.libs.json.{Reads, Json}

case class MarketCatalogue(marketId: String,
                           marketName: String,
                           marketStartTime: DateTime,
                           description: Option[MarketDescription] = None,
                           totalMatched: Double,
                           runners: List[RunnerCatalog],
                           eventType: EventType,
                           competition: Option[Competition] = None,
                           event: Event)

object MarketCatalogue {

  import play.api.libs.json._

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)

  implicit val readsMarketCatalogue = Json.reads[MarketCatalogue]
}