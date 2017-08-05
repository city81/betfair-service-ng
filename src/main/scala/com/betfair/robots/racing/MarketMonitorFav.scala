package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor._
import com.betfair.domain._
import com.betfair.service.{BetfairServiceNG, BetfairServiceNGException}
import org.joda.time
import org.joda.time.DateTimeZone

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class MarketMonitorFav(betfairServiceNG: BetfairServiceNG) extends Actor {

  import MarketMonitorFav._
  import context._

  def receive = {

    case StartMonitoring(sessionToken) =>

      betfairServiceNG.keepAlive(sessionToken).map {
        case Some(keepAliveResponse) =>

          // list all horse racing markets in the next 24 hour period
          val marketStartTime = new TimeRange(Some(new time.DateTime()), Some((new time.DateTime()).plusDays(1)))
          val listMarketCatalogueMarketFilter = new MarketFilter(eventTypeIds = Set(7),
            marketStartTime = Some(marketStartTime),
            marketBettingTypes = Set(MarketBettingType.ODDS),
            marketCountries = Set("GB"),
            marketTypeCodes = Set("WIN"))

          betfairServiceNG.listMarketCatalogue(sessionToken, listMarketCatalogueMarketFilter,
            List(MarketProjection.MARKET_START_TIME,
              MarketProjection.RUNNER_DESCRIPTION,
              MarketProjection.EVENT_TYPE,
              MarketProjection.EVENT,
              MarketProjection.COMPETITION), MarketSort.FIRST_TO_START, 200
          ) onComplete {
            case Success(Some(listMarketCatalogueContainer)) =>
              for (marketCatalogue <- listMarketCatalogueContainer.result) {

                // create the race actor if not already running
                var raceActorName = "monitor-fav-" + marketCatalogue.marketId

//                if (child(raceActorName).isEmpty) {
//
//                  println(marketCatalogue.marketStartTime.get + " " + raceActorName)
//
//                  val millisecondsBeforeMonitoring =
//                    marketCatalogue.marketStartTime.get.getMillis -
//                      (new time.DateTime(DateTimeZone.UTC)).getMillis
//
//                  try {
//                    system.scheduler.scheduleOnce(
//                      Duration(millisecondsBeforeMonitoring, TimeUnit.MILLISECONDS),
//                      system.actorOf(Props(new MonitorInPlayWinScalp(betfairServiceNG, sessionToken,
//                        marketCatalogue.marketId, marketCatalogue.marketStartTime)),
//                        raceActorName), "")
//                  } catch {
//                    case e: Exception =>
//                      println(marketCatalogue.marketStartTime.get + " " + raceActorName + " - already created")
//                  }
//                }

                if (marketCatalogue.marketName.contains("Hcap") || marketCatalogue.marketName.contains("Nursery")) {
                  raceActorName = raceActorName + "-Hcap"
                } else {
                  raceActorName = raceActorName + "-NonHcap"
                }

                if (!marketCatalogue.marketName.contains("PA")) {

                  if (child(raceActorName).isEmpty &&
                    (marketCatalogue.marketName.contains("Hcap") || marketCatalogue.marketName.contains("Nursery"))) {

                    println(marketCatalogue.marketStartTime.get + " " + raceActorName)

                    val millisecondsBeforeMonitoring =
                      marketCatalogue.marketStartTime.get.getMillis -
                        (new time.DateTime(DateTimeZone.UTC)).getMillis

                    try {
                      system.scheduler.scheduleOnce(
                        Duration(millisecondsBeforeMonitoring, TimeUnit.MILLISECONDS),
                        system.actorOf(Props(new MonitorInPlayWinHcap(betfairServiceNG, sessionToken,
                          marketCatalogue.marketId, marketCatalogue.marketStartTime)),
                          raceActorName), "")
                    } catch {
                      case e: Exception =>
                        println(marketCatalogue.marketStartTime.get + " " + raceActorName + " - already created")
                    }
                  } else if (child(raceActorName).isEmpty &&
                    (!marketCatalogue.marketName.contains("Hcap") && !marketCatalogue.marketName.contains("Nursery"))) {

                    println(marketCatalogue.marketStartTime.get + " " + raceActorName)

                    val millisecondsBeforeMonitoring =
                      marketCatalogue.marketStartTime.get.getMillis -
                        (new time.DateTime(DateTimeZone.UTC)).getMillis

                    try {
                      system.scheduler.scheduleOnce(
                        Duration(millisecondsBeforeMonitoring, TimeUnit.MILLISECONDS),
                        system.actorOf(Props(new MonitorInPlayWinNonHcap(betfairServiceNG, sessionToken,
                          marketCatalogue.marketId, marketCatalogue.marketStartTime)),
                          raceActorName), "")
                    } catch {
                      case e: Exception =>
                        println(marketCatalogue.marketStartTime.get + " " + raceActorName + " - already created")
                    }
                  }

                }


              }
            case Success(None) =>
              println("error no result returned")
            case Failure(error) =>
              println("error " + error)
          }
        case _ => throw new BetfairServiceNGException("no session token")
      }
  }
}

object MarketMonitorFav {

  case class StartMonitoring(sessionToken: String)

}



