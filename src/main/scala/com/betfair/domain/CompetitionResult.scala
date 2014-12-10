package com.betfair.domain

import play.api.libs.json.Json

case class CompetitionResult(competition: Competition, marketCount: Int, competitionRegion: String)

object CompetitionResult {
  implicit val formatCompetitionResult = Json.format[CompetitionResult]
}