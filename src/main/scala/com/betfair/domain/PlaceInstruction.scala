package com.betfair.domain

import com.betfair.domain.OrderType.OrderType
import com.betfair.domain.Side.Side
import play.api.libs.json.Json

case class PlaceInstruction(orderType: OrderType, selectionId: Long, handicap: Double, side: Side,
                            limitOrder: Option[LimitOrder] = None, limitOnCloseOrder: Option[LimitOnCloseOrder] = None,
                            marketOnCloseOrder: Option[MarketOnCloseOrder] = None)

object PlaceInstruction {
  implicit val formatPlaceInstruction = Json.format[PlaceInstruction]
}