package com.betfair.domain

import play.api.libs.json.Json

case class LogoutResponse(token: String, product: String, status: String, error: String)

object LogoutResponse {
  implicit val readsLogoutResponse = Json.reads[LogoutResponse]
}

