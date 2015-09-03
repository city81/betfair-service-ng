package com.betfair.domain

import com.betfair.domain.ExecutionReportErrorCode.ExecutionReportErrorCode
import com.betfair.domain.ExecutionReportStatus.ExecutionReportStatus
import play.api.libs.json.Json

case class UpdateExecutionReport(status: ExecutionReportStatus, marketId: String, errorCode: Option[ExecutionReportErrorCode],
                                instructionReports: Set[UpdateInstructionReport],
                                customerRef: Option[String])

object UpdateExecutionReport {
  implicit val readsUpdateExecutionReport = Json.reads[UpdateExecutionReport]
}
