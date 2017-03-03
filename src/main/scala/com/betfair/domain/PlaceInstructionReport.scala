package com.betfair.domain

import com.betfair.domain.InstructionReportErrorCode.InstructionReportErrorCode
import com.betfair.domain.InstructionReportStatus.InstructionReportStatus
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

case class PlaceInstructionReport(status: Option[InstructionReportStatus],
                                  errorCode: Option[InstructionReportErrorCode],
                                  instruction: Option[PlaceInstruction],
                                  betId: Option[String],
                                  placedDate: Option[DateTime],
                                  averagePriceMatched: Option[Double],
                                  sizeMatched: Option[Double])

object PlaceInstructionReport {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsPlaceInstructionReport = Json.reads[PlaceInstructionReport]
}