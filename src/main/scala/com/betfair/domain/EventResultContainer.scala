package com.betfair.domain

import play.api.libs.json.Json

case class EventResultContainer(result: List[EventResult])

object EventResultContainer {
  implicit val readsEventResultContainer = Json.format[EventResultContainer]
}