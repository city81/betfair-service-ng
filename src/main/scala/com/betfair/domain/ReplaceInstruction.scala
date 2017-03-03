package com.betfair.domain

import play.api.libs.json.Json

case class ReplaceInstruction(betId: String, newPrice: Double)

object ReplaceInstruction {
  implicit val formatReplaceInstruction = Json.format[ReplaceInstruction]
}