package com.betfair.domain

import play.api.libs.json.Json

case class Competition(id: String, name: String)

object Competition {
  implicit val formatCompetition = Json.format[Competition]
}