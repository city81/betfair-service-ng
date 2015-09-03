package com.betfair.domain

import play.api.libs.json.Json

case class CurrentOrderSummaryReportContainer(result: CurrentOrderSummaryReport)

object CurrentOrderSummaryReportContainer {
  implicit val readsCurrentOrderSummaryReportContainer = Json.reads[CurrentOrderSummaryReportContainer]
}