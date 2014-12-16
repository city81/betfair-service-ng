package com.betfair.domain

import play.api.libs.json.Json

case class PlaceExecutionReportContainer(result: PlaceExecutionReport)

object PlaceExecutionReportContainer {
  implicit val readsPlaceExecutionReportContainer = Json.reads[PlaceExecutionReportContainer]
}