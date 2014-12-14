package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain.MarketProjection.MarketProjection
import com.betfair.domain.MarketSort.MarketSort
import com.betfair.domain.OrderProjection.OrderProjection
import com.betfair.domain.MatchProjection.MatchProjection
import com.betfair.domain._
import scala.collection.mutable.HashMap
import scala.concurrent._
import scala.concurrent.duration._


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
                     priceProjection: Option[(String,PriceProjection)] = None,
                     orderProjection: Option[(String,OrderProjection)] = None,
                     matchProjection: Option[(String,MatchProjection)] = None,
                     currencyCode: Option[(String,String)] = None): Future[Option[ListMarketBookContainer]] = {

    import spray.httpx.PlayJsonSupport._

    // this simplifies the json serialisation of the Options when in the params HashMap
    val flattenedOpts = Seq(priceProjection, orderProjection, matchProjection, currencyCode).flatten

    val params = HashMap[String, Object]("marketIds" -> marketIds)

    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listMarketBook",
      params = params ++ flattenedOpts.map(i => i._1 -> i._2).toMap)
    command.makeAPIRequest[ListMarketBookContainer](request)
  }

  def placeOrders(marketId: String, instructions: Set[PlaceInstruction]): Future[Option[PlaceExecutionReportContainer]] = {

    import spray.httpx.PlayJsonSupport._

    val params = HashMap[String, Object]("marketId" -> marketId, "instructions" -> instructions)

    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/placeOrders", params = params)
    command.makeAPIRequest[PlaceExecutionReportContainer](request)
  }

  def getExchangeFavourite(marketId: String): Future[Option[Runner]] = Future {

    def shortestPrice(runners: Set[Runner]): Runner = {
      if (runners.isEmpty) throw new NoSuchElementException
      runners.reduceLeft((x, y) => if (x.ex.get.availableToBack.head.price < y.ex.get.availableToBack.head.price) x else y)
    }

    val priceProjection = PriceProjection(priceData = Set(PriceData.EX_BEST_OFFERS))
    val favourite = listMarketBook(marketIds = Set(marketId), priceProjection = Some(("priceProjection", priceProjection))
    ).map { response =>
      response match {
        case Some(listMarketBookContainer) =>
          Some(shortestPrice(listMarketBookContainer.result(0).runners))
        case error =>
          println("error", error)
          None
      }
    }
    Await.result(favourite, 10 seconds)

  }

}