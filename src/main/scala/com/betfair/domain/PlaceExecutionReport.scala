package com.betfair.domain

import com.betfair.domain.ExecutionReportErrorCode.ExecutionReportErrorCode
import com.betfair.domain.ExecutionReportStatus.ExecutionReportStatus
import play.api.libs.json.Json

case class PlaceExecutionReport(status: ExecutionReportStatus, marketId: String, errorCode: Option[ExecutionReportErrorCode],
                                instructionReports: Set[PlaceInstructionReport],
customerRef: Option[String])

object PlaceExecutionReport {
  implicit val readsPlaceExecutionReport = Json.reads[PlaceExecutionReport]
}
