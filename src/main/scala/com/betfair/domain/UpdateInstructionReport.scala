package com.betfair.domain

import com.betfair.domain.InstructionReportErrorCode.InstructionReportErrorCode
import com.betfair.domain.InstructionReportStatus.InstructionReportStatus
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads}

case class UpdateInstructionReport(status: Option[InstructionReportStatus], errorCode: Option[InstructionReportErrorCode],
                                   instruction: UpdateInstruction)

object UpdateInstructionReport {
  implicit val readsUpdateInstructionReport = Json.reads[UpdateInstructionReport]
}