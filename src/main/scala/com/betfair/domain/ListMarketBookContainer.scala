package com.betfair.domain

case class ListMarketBookContainer(result: List[MarketBook])

object ListMarketBookContainer {

  import play.api.libs.json._

  implicit val readsListMarketBookContainer = Json.reads[ListMarketBookContainer]
}