package com.betfair.domain

import com.betfair.domain.InstructionReportErrorCode.InstructionReportErrorCode
import com.betfair.domain.InstructionReportStatus.InstructionReportStatus
import play.api.libs.json.{Json, Reads}

case class ReplaceInstructionReport(status: Option[InstructionReportStatus],
                                    errorCode: Option[InstructionReportErrorCode],
                                    cancelInstructionReport: CancelInstructionReport,
                                    placeInstructionReport: PlaceInstructionReport)

object ReplaceInstructionReport {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)
  implicit val readsReplaceInstructionReport = Json.reads[ReplaceInstructionReport]
}