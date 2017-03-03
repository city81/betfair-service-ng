package com.betfair.domain

import com.betfair.domain.OrderStatus.OrderStatus
import com.betfair.domain.OrderType.OrderType
import com.betfair.domain.PersistenceType.PersistenceType
import com.betfair.domain.Side.Side
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

case class CurrentOrderSummary(betId: String, marketId: String, selectionId: Long, handicap: Double,
                               priceSize: PriceSize, bspLiability: Double, side: Side,
                               status: OrderStatus, persistenceType: PersistenceType, orderType: OrderType,
                               placedDate: DateTime,
                               matchedDate: Option[DateTime] = None,
                               averagePriceMatched: Double, sizeMatched: Double, sizeRemaining: Double,
                               sizeLapsed: Double, sizeCancelled: Double, sizeVoided: Double,
                               regulatorCode: String)

object CurrentOrderSummary {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsCurrentOrderSummary = Json.reads[CurrentOrderSummary]
}