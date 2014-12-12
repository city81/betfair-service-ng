package com.betfair.domain

import org.joda.time.DateTime
import play.api.libs.json.{Reads, Json}

case class Runner(selectionId: Long,
                  handicap: Double,
                  status: String,
                  adjustmentFactor: Option[Double],
                  lastPriceTraded: Option[Double],
                  totalMatched: Option[Double],
                  removalDate: Option[DateTime],
                  sp: Option[StartingPrices],
                  ex: Option[ExchangePrices],
                  orders: Option[Set[Order]],
                  matches: Option[Set[Match]])

object Runner {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)
  implicit val readsRunner = Json.reads[Runner]
}