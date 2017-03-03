package com.betfair.domain

import com.betfair.domain.Side.Side
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, Reads}

case class Match(betId: String, matchId: String, side: Side, price: Double, size: Double, matchDate: DateTime)

object Match {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val readsMatch = Json.reads[Match]
}