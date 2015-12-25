package com.betfair.domain

import play.api.libs.json.Json

case class KeepAliveResponse(token: String, product: String, status: String, error: String)

object KeepAliveResponse {
  implicit val readsKeepAliveResponse = Json.reads[KeepAliveResponse]
}

