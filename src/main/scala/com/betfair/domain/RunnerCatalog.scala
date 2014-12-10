package com.betfair.domain

import play.api.libs.json.Json

case class RunnerCatalog(selectionId: Long,
                         runnerName: String,
                         handicap: Double,
                         sortPriority: Option[Int] = None,
                         metadata: Option[Map[String, String]] = None)

object RunnerCatalog {

  implicit val readsRunnerCatalog = Json.reads[RunnerCatalog]
}