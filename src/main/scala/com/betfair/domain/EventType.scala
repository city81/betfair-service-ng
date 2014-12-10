package com.betfair.domain

import play.api.libs.json.Json

case class EventType(id: String, name: String)

object EventType {
  implicit val readsEventType = Json.format[EventType]
}