package com.betfair.domain

import play.api.libs.json.Json

case class EventTypeResultContainer(result: List[EventTypeResult])

object EventTypeResultContainer {
  implicit val readsEventTypeResultContainer = Json.format[EventTypeResultContainer]
}