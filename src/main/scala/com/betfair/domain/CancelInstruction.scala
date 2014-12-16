package com.betfair.domain

import play.api.libs.json.Json

case class CancelInstruction(betId: String, sizeReduction: Option[Double] = None)

object CancelInstruction {
  implicit val formatCancelInstruction = Json.format[CancelInstruction]
}