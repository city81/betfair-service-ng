package com.betfair.domain

import com.betfair.domain.ExecutionReportErrorCode.ExecutionReportErrorCode
import com.betfair.domain.ExecutionReportStatus.ExecutionReportStatus
import play.api.libs.json.Json

case class ReplaceExecutionReport(status: ExecutionReportStatus, marketId: String,
                                errorCode: Option[ExecutionReportErrorCode],
                                instructionReports: Set[ReplaceInstructionReport],
                                customerRef: Option[String])

object ReplaceExecutionReport {
  implicit val readsReplaceExecutionReport = Json.reads[ReplaceExecutionReport]
}
