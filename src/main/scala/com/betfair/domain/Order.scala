package com.betfair.domain

import com.betfair.domain.OrderStatus.OrderStatus
import com.betfair.domain.OrderType.OrderType
import com.betfair.domain.PersistenceType.PersistenceType
import com.betfair.domain.Side.Side
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Order(betId: String, orderType: OrderType, status: OrderStatus, persistenceType: PersistenceType,
                 side: Side, price: Double, size: Double, bspLiability: Double, placedDate: DateTime,
                 avgPriceMatched: Double, sizeMatched: Double, sizeRemaining: Double, sizeLapsed: Double,
                 sizeCancelled: Double, sizeVoided: Double)

object Order {
  implicit val readsOrder = Json.reads[Order]
}