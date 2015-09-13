package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor._
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.DateTimeZone

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class MarketMonitor(betfairServiceNG: BetfairServiceNG) extends Actor {

  import com.betfair.robots.racing.MarketMonitor._
  import context._

  def receive = {
    case StartMonitoring(sessionToken) =>

      // list all horse racing markets in the next 24 hour period
      val marketStartTime = new TimeRange(Some(new time.DateTime()), Some((new time.DateTime()).plusDays(1)))
      val listMarketCatalogueMarketFilter = new MarketFilter(eventTypeIds = Set(7),
        marketStartTime = Some(marketStartTime),
        marketBettingTypes = Set(MarketBettingType.ODDS),
        marketCountries = Set("GB"),
        marketTypeCodes = Set("WIN"))

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

            // create the race actor if not already running
            val raceActorName = "exchange-favourite-" + marketCatalogue.marketId

            if (child(raceActorName).isEmpty) {

              val raceActor = context.actorOf(Props(new MonitorBetfairFavourite(betfairServiceNG, sessionToken,
                marketCatalogue.marketId, marketCatalogue.marketStartTime)), raceActorName)

              // start actor five mins before the off
              val millisecondsBeforeMonitoring =
                        marketCatalogue.marketStartTime.get.minusMinutes(5).getMillis -
                          (new time.DateTime(DateTimeZone.UTC)).getMillis

              // start an actor to get exchange favourite then get price every second
              system.scheduler.scheduleOnce(
                Duration(millisecondsBeforeMonitoring, TimeUnit.MILLISECONDS),
                system.actorOf(Props(new MonitorBetfairFavourite(betfairServiceNG, sessionToken,
                  marketCatalogue.marketId, marketCatalogue.marketStartTime)),
                  "exchange-favourite-" + marketCatalogue.marketId), "")
            }
          }
        case Success(None) =>
          println("error no result returned")
        case Failure(error) =>
          println("error " + error)
      }
  }
}

object MarketMonitor {
  case class StartMonitoring(sessionToken: String)
}



