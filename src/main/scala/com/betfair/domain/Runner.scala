package com.betfair.domain

import play.api.libs.json.Json

case class Runner(selectionId: Long,
                  handicap: Double,
                  status: String)

object Runner {
  implicit val formatRunner = Json.format[Runner]
}