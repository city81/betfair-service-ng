package com.betfair.domain

import com.betfair.domain.MarketBettingType.MarketBettingType
import com.betfair.domain.OrderStatus.OrderStatus
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class MarketFilter(textQuery: Option[String] = None,
                        exchangeIds: Set[Int] = Set.empty,
                        eventTypeIds: Set[Int] = Set.empty,
                        marketIds: Set[Int] = Set.empty,
                        inPlayOnly: Option[Boolean] = None,
                        eventIds: Set[Int] = Set.empty,
                        competitionIds: Set[Int] = Set.empty,
                        venues: Set[String] = Set.empty,
                        bspOnly: Option[Boolean] = None,
                        turnInPlayEnabled: Option[Boolean] = None,
                        marketBettingTypes: Set[MarketBettingType] = Set.empty,
                        marketCountries: Set[String] = Set.empty,
                        marketTypeCodes: Set[String] = Set.empty,
                        marketStartTime: Option[TimeRange] = None,
                        withOrders: Set[OrderStatus] = Set.empty)


object MarketFilter {

  implicit val writeTimeRangeFormat = new Writes[TimeRange] {

    def writes(timeRange: TimeRange): JsValue = {
      if (timeRange == null) JsNull else Json.toJson(timeRange)
    }
  }

  implicit val writesMarketFilter = Json.writes[MarketFilter]
}