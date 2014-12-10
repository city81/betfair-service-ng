package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain._
import org.joda.time.DateTime
import scala.collection.immutable.{HashSet, HashMap}
import scala.concurrent._
import scala.util.{Failure, Success}


object ExampleBetfairServiceNG extends App {

  override def main(args: Array[String]) {

    implicit val system = ActorSystem()
    import system.dispatcher
    val config = new Configuration("tbd", "tbd", "tbd")
    val command = new BetfairServiceNGCommand(config)

    val betfairServiceNG = new BetfairServiceNG(config, command)

    // log in to obtain a session id
    betfairServiceNG.login

    // list event types
    betfairServiceNG.listEventTypes(new MarketFilter()) onComplete {
      case Success(Some(eventTypeResultContainer)) =>
        for (eventTypeResult <- eventTypeResultContainer.result) {
          println("Event Type is: " + eventTypeResult)
        }
      case Failure(error) =>
        println("error", error)
    }

    // list competitions
    betfairServiceNG.listCompetitions(new MarketFilter()) onComplete {
      case Success(Some(competitionResultContainer)) =>
        for (competitionResult <- competitionResultContainer.result) {
          println("Competition is: " + competitionResult)
        }
      case Failure(error) =>
        println("error", error)
    }

    // list football competitions
    val listCompetitionsMarketFilter = new MarketFilter(eventTypeIds = Set(1))
    betfairServiceNG.listCompetitions(listCompetitionsMarketFilter) onComplete {
      case Success(Some(competitionResultContainer)) =>
        for (competitionResult <- competitionResultContainer.result) {
          println("Football Competition is: " + competitionResult)
        }
      case Failure(error) =>
        println("error", error)
    }

    // list all horse racing markets in the next 24 hour period
    val marketStartTime = new TimeRange(DateTime.now(),DateTime.now().plusDays(1))
    val listMarketCatalogueMarketFilter = new MarketFilter(eventTypeIds = Set(7), marketStartTime = marketStartTime)
    betfairServiceNG.listMarketCatalogue(listMarketCatalogueMarketFilter,
      List(MarketProjection.MARKET_START_TIME,
      //MarketProjection.RUNNER_METADATA, // TODO need to get a json reader working for metadata
      MarketProjection.RUNNER_DESCRIPTION,
      MarketProjection.EVENT_TYPE,
      MarketProjection.EVENT,
      MarketProjection.COMPETITION), MarketSort.FIRST_TO_START, 200
    ) onComplete {
      case Success(Some(listMarketCatalogueContainer)) =>
        for (marketCatalogue <- listMarketCatalogueContainer.result) {
          println("Market Catalogue is: " + marketCatalogue)
        }
      case Failure(error) =>
        println("error", error)
    }

    // list market book
    betfairServiceNG.listMarketBook(Set("1.116678172")
    ) onComplete {
      case Success(Some(listMarketBookContainer)) =>
        for (marketBook <- listMarketBookContainer.result) {
          println("Market Book is: " + marketBook)
        }
      case Failure(error) =>
        println("error", error)
    }

  }

}