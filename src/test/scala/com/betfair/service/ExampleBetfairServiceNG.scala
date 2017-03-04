package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import scala.concurrent.duration._
import scala.concurrent._
import scala.util.{Failure, Success}
import org.scalatest.Ignore
import scala.language.postfixOps

@Ignore
object ExampleBetfairServiceNG extends App {

    implicit val system = ActorSystem()
    import system.dispatcher
    val conf = ConfigFactory.load()
    val appKey = conf.getString("betfairService.appKey")
    val username = conf.getString("betfairService.username")
    val password = conf.getString("betfairService.password")
    val apiUrl = conf.getString("betfairService.apiUrl")
    val isoUrl = conf.getString("betfairService.isoUrl")

    val config = new Configuration(appKey, username, password, apiUrl, isoUrl)
    val command = new BetfairServiceNGCommand(config)

    val betfairServiceNG = new BetfairServiceNG(config, command)

    // log in to obtain a session id
    val sessionTokenFuture = betfairServiceNG.login map {
      case Some(loginResponse) => loginResponse.token
      case _ => throw new BetfairServiceNGException("no session token")
    }

    val sessionToken = Await.result(sessionTokenFuture, 10 seconds)

    // list event types
    betfairServiceNG.listEventTypes(sessionToken, new MarketFilter()) onComplete {
      case Success(Some(eventTypeResultContainer)) =>
        for (eventTypeResult <- eventTypeResultContainer.result) {
          println("Event Type is: " + eventTypeResult)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // list competitions
    betfairServiceNG.listCompetitions(sessionToken, new MarketFilter()) onComplete {
      case Success(Some(competitionResultContainer)) =>
        for (competitionResult <- competitionResultContainer.result) {
          println("Competition is: " + competitionResult)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // list football competitions
    val listCompetitionsMarketFilter = new MarketFilter(eventTypeIds = Set(1))
    betfairServiceNG.listCompetitions(sessionToken, listCompetitionsMarketFilter) onComplete {
      case Success(Some(competitionResultContainer)) =>
        for (competitionResult <- competitionResultContainer.result) {
          println("Football Competition is: " + competitionResult)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // list all horse racing markets in the next 24 hour period
    val marketStartTime = new TimeRange(Some(DateTime.now()), Some(DateTime.now().plusDays(1)))
    val listMarketCatalogueMarketFilter = new MarketFilter(eventTypeIds = Set(7),
      marketStartTime = Some(marketStartTime))
    betfairServiceNG.listMarketCatalogue(sessionToken, listMarketCatalogueMarketFilter,
      List(MarketProjection.MARKET_START_TIME,
        //MarketProjection.RUNNER_METADATA, // TODO need to get a json reader working for runner metadata
        MarketProjection.RUNNER_DESCRIPTION,
        MarketProjection.EVENT_TYPE,
        MarketProjection.EVENT,
        MarketProjection.COMPETITION), MarketSort.FIRST_TO_START, 200
    ) onComplete {
      case Success(Some(listMarketCatalogueContainer)) =>
        for (marketCatalogue <- listMarketCatalogueContainer.result) {
          println("Market Catalogue is: " + marketCatalogue)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // get the next UK Win horse racing market
    val nextUKMarketStartTime = new TimeRange(from = Some(DateTime.now()))
    val nextUKWinHorseRacingMarketFilter = new MarketFilter(eventTypeIds = Set(7),
      marketStartTime = Some(nextUKMarketStartTime),
      marketBettingTypes = Set(MarketBettingType.ODDS),
      marketCountries = Set("GB"),
      marketTypeCodes = Set("WIN")
      )
    betfairServiceNG.listMarketCatalogue(sessionToken, nextUKWinHorseRacingMarketFilter,
      List(MarketProjection.EVENT), MarketSort.FIRST_TO_START, 50
    ) onComplete {
      case Success(Some(listMarketCatalogueContainer)) =>
        for (marketCatalogue <- listMarketCatalogueContainer.result) {
          println("Next UK Win Horse Racing Market Catalogue is: " + marketCatalogue)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // list market book with Exchange Best Offers
    val priceProjection = PriceProjection(priceData = Set(PriceData.EX_BEST_OFFERS))
    betfairServiceNG.listMarketBook(sessionToken, marketIds = Set("1.116666615"),
      priceProjection = Some(("priceProjection", priceProjection))
    ) onComplete {
      case Success(Some(listMarketBookContainer)) =>
        for (marketBook <- listMarketBookContainer.result) {
          println("Market Book is: " + marketBook)
        }
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // get exchange favourite
    betfairServiceNG.getExchangeFavourite(sessionToken, marketId = "1.116666615"
    ) onComplete {
      case Success(Some(runner)) =>
        println("Runner is: " + runner)
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // get price bound runners
    betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = "1.116750507", lowerPrice = 2.0, higherPrice = 4.0
    ) onComplete {
      case Success(Some(runners)) =>
        println("Runners are: " + runners)
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // place a bet
    val placeInstructions = Set(PlaceInstruction(orderType = OrderType.LIMIT, selectionId = 56343,
      handicap = Some(0.0), side = Side.BACK,
      limitOrder = Some(LimitOrder(size = 2.0, price = 3.5, persistenceType = PersistenceType.PERSIST))))
    betfairServiceNG.placeOrders(sessionToken, marketId = "1.116586576", instructions = placeInstructions
    ) onComplete {
      case Success(Some(placeExecutionReportContainer)) =>
        println("Place Execution Report is: " + placeExecutionReportContainer)
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

    // cancel a bet
    val cancelInstructions = Set(CancelInstruction(betId = "44533201723", sizeReduction = None))
    betfairServiceNG.cancelOrders(sessionToken, marketId = "1.116666615", instructions = cancelInstructions
    ) onComplete {
      case Success(Some(placeExecutionReportContainer)) =>
        println("Cancel Execution Report is: " + placeExecutionReportContainer)
      case Success(None) =>
        println("error no result returned")
      case Failure(error) =>
        println("error " + error)
    }

}