package com.betfair.domain

import play.api.libs.json.Json

case class CompetitionResultContainer(result: List[CompetitionResult])

object CompetitionResultContainer {
  implicit val formatCompetitionResultContainer = Json.format[CompetitionResultContainer]
}