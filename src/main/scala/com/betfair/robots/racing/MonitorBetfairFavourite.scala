package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorBetfairFavourite(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  def receive = {
    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      while (marketStartTime.get.getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        // get exchange favourite
        betfairServiceNG.getExchangeFavourite(sessionToken, marketId = marketId
        ) onComplete {
          case Success(Some(runner)) =>
            println("runner = " + runner.selectionId + " " + runner.lastPriceTraded)
            println("back prices = " + runner.ex.get.availableToBack)
            println("lay prices = " + runner.ex.get.availableToLay)
          case Success(None) =>
            println("error no result returned")
          case Failure(error) =>
            println("error in startReceiving " + error)
        }

        // wait for one sec to overcome throttling
        Thread.sleep(1000)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)

    }
  }

}