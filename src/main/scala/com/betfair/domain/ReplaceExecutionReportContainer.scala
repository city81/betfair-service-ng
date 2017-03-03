package com.betfair.domain

import play.api.libs.json.Json

case class ReplaceExecutionReportContainer(result: ReplaceExecutionReport)

object ReplaceExecutionReportContainer {
  implicit val readsReplaceExecutionReportContainer = Json.reads[ReplaceExecutionReportContainer]
}