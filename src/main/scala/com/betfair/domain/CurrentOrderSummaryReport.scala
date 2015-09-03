package com.betfair.domain

import play.api.libs.json.Json

case class CurrentOrderSummaryReport(currentOrders: Set[CurrentOrderSummary], moreAvailable: Boolean)

object CurrentOrderSummaryReport {
  implicit val readsCurrentOrderSummaryReport = Json.reads[CurrentOrderSummaryReport]
}
