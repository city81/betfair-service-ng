package com.betfair.domain

import com.betfair.domain.ExecutionReportErrorCode.ExecutionReportErrorCode
import com.betfair.domain.ExecutionReportStatus.ExecutionReportStatus
import play.api.libs.json.Json

case class CancelExecutionReport(status: ExecutionReportStatus, marketId: String, errorCode: Option[ExecutionReportErrorCode],
                                instructionReports: Set[CancelInstructionReport],
                                customerRef: Option[String])

object CancelExecutionReport {
  implicit val readsCancelExecutionReport = Json.reads[CancelExecutionReport]
}
