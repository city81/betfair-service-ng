package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain.MarketProjection.MarketProjection
import com.betfair.domain.MarketSort.MarketSort
import com.betfair.domain.OrderProjection.OrderProjection
import com.betfair.domain.MatchProjection.MatchProjection
import com.betfair.domain._
import scala.collection.immutable.HashMap
import scala.concurrent._
import scala.util.{Failure, Success}


final class BetfairServiceNG(val config: Configuration, command: BetfairServiceNGCommand)
                            (implicit executionContext: ExecutionContext, system: ActorSystem) {


  def login() {

    import spray.httpx.PlayJsonSupport._

    val request = LoginRequest(config.username, config.password)
    command.makeLoginRequest(request)
  }

  def logout() {

    import spray.httpx.PlayJsonSupport._

    command.makeLogoutRequest
  }

  def listEventTypes(marketFilter: MarketFilter): Future[Option[EventTypeResultContainer]] = {

    import spray.httpx.PlayJsonSupport._

    val params = HashMap[String, Object]("filter" -> marketFilter)
    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listEventTypes", params = params)
    command.makeAPIRequest[EventTypeResultContainer](request)
  }

  def listCompetitions(marketFilter: MarketFilter): Future[Option[CompetitionResultContainer]] = {

    import spray.httpx.PlayJsonSupport._

    val params = HashMap[String, Object]("filter" -> marketFilter)
    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listCompetitions", params = params)
    command.makeAPIRequest[CompetitionResultContainer](request)
  }

  def listMarketCatalogue(marketFilter: MarketFilter, marketProjection: List[MarketProjection], sort: MarketSort,
                           maxResults: Integer): Future[Option[ListMarketCatalogueContainer]] = {

    import spray.httpx.PlayJsonSupport._

    val params = HashMap[String, Object]("filter" -> marketFilter, "marketProjection" -> marketProjection,
      "sort" -> sort, "maxResults" -> maxResults)
    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listMarketCatalogue", params = params)
    command.makeAPIRequest[ListMarketCatalogueContainer](request)
  }

  def listMarketBook(marketIds: Set[String],
                     priceProjection: Option[PriceProjection] = None,
                     orderProjection: Option[OrderProjection] = None,
                     matchProjection: Option[MatchProjection] = None,
                     currencyCode: Option[String] = None): Future[Option[ListMarketBookContainer]] = {

    import spray.httpx.PlayJsonSupport._

    val params = HashMap[String, Object]("marketIds" -> marketIds, "priceProjection" -> priceProjection,
      "orderProjection" -> orderProjection, "matchProjection" -> matchProjection, "currencyCode" -> currencyCode)
    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listMarketBook", params = params)
    command.makeAPIRequest[ListMarketBookContainer](request)
  }
}