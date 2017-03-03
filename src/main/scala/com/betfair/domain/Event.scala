package com.betfair.domain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

case class Event(id: String,
            name: String,
            countryCode: Option[String] = None,
            timezone: String,
            venue: Option[String] = None,
            openDate: DateTime)

object Event {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

  implicit val jodaFormat: Format[DateTime] = Format(jodaDateReads, jodaDateWrites)

  implicit val formatEvent = Json.format[Event]
}