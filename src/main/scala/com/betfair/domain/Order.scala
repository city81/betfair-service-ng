package com.betfair.domain

import com.betfair.domain.OrderStatus.OrderStatus
import com.betfair.domain.OrderType.OrderType
import com.betfair.domain.PersistenceType.PersistenceType
import com.betfair.domain.Side.Side
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

case class Order(betId: String, orderType: OrderType, status: OrderStatus, persistenceType: PersistenceType,
                 side: Side, price: Double, size: Double, bspLiability: Double, placedDate: DateTime,
                 avgPriceMatched: Double, sizeMatched: Double, sizeRemaining: Double, sizeLapsed: Double,
                 sizeCancelled: Double, sizeVoided: Double)

object Order {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsOrder = Json.reads[Order]
}