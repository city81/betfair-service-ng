package com.betfair.domain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

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

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsRunner = Json.reads[Runner]
}