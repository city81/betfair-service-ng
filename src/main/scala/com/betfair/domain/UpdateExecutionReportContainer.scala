package com.betfair.domain

import play.api.libs.json.Json

case class UpdateExecutionReportContainer(result: UpdateExecutionReport)

object UpdateExecutionReportContainer {
  implicit val readsUpdateExecutionReportContainer = Json.reads[UpdateExecutionReportContainer]
}