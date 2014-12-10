package com.betfair.domain


case class ListMarketCatalogueContainer(result: List[MarketCatalogue])

object ListMarketCatalogueContainer {

  import play.api.libs.json._

  implicit val readsListMarketCatalogueContainer = Json.reads[ListMarketCatalogueContainer]
}