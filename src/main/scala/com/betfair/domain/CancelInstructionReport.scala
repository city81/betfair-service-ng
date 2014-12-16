package com.betfair.domain

import com.betfair.domain.InstructionReportErrorCode.InstructionReportErrorCode
import com.betfair.domain.InstructionReportStatus.InstructionReportStatus
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads}

case class CancelInstructionReport(status: Option[InstructionReportStatus], errorCode: Option[InstructionReportErrorCode],
                                   instruction: CancelInstruction, sizeCancelled: Option[Double], cancelledDate: Option[DateTime])

object CancelInstructionReport {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)
  implicit val readsCancelInstructionReport = Json.reads[CancelInstructionReport]
}