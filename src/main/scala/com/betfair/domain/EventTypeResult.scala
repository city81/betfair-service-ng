package com.betfair.domain

import play.api.libs.json.Json

case class EventTypeResult(eventType: EventType, marketCount: Int)

object EventTypeResult {
  implicit val readsEventTypeResult = Json.format[EventTypeResult]
}