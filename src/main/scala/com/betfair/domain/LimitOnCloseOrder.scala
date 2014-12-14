package com.betfair.domain

import play.api.libs.json.Json

case class LimitOnCloseOrder(liability: Double, price: Double)

object LimitOnCloseOrder {
  implicit val formatLimitOnCloseOrder = Json.format[LimitOnCloseOrder]
}