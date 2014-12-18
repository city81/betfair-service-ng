package com.betfair.domain

import org.joda.time.DateTime
import play.api.libs.json._

case class TimeRange(from: Option[DateTime] = None, to: Option[DateTime] = None)

object TimeRange {

  implicit val jodaDateWrites: Writes[org.joda.time.DateTime] = new Writes[org.joda.time.DateTime] {
    def writes(d: org.joda.time.DateTime): JsValue = JsString(d.toString())
  }

  implicit val writesTimeRange = Json.writes[TimeRange]
}