package com.betfair.domain

import com.betfair.domain.InstructionReportErrorCode.InstructionReportErrorCode
import com.betfair.domain.InstructionReportStatus.InstructionReportStatus
import org.joda.time.DateTime
import play.api.libs.json.{Reads, Json}

case class PlaceInstructionReport(status: Option[InstructionReportStatus], errorCode: Option[InstructionReportErrorCode],
                                   instruction: PlaceInstruction, betId: String, placedDate: DateTime,
                                   averagePriceMatched: Double, sizeMatched: Double)

object PlaceInstructionReport {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val dateTimeReads = Reads.jodaDateReads(dateFormat)
  implicit val readsPlaceInstructionReport = Json.reads[PlaceInstructionReport]
}