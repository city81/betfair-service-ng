package com.betfair.domain

import com.betfair.domain.Side.Side
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Match(betId: String, matchId: String, side: Side, price: Double, size: Double, matchDate: DateTime)

object Match {
  implicit val readsMatch = Json.reads[Match]
}