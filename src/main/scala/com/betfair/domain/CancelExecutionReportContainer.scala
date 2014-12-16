package com.betfair.domain

import play.api.libs.json.Json

case class CancelExecutionReportContainer(result: CancelExecutionReport)

object CancelExecutionReportContainer {
  implicit val readsCancelExecutionReportContainer = Json.reads[CancelExecutionReportContainer]
}