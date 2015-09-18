package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}
import scala.concurrent.duration._

import scala.concurrent._
import scala.language.postfixOps

class MonitorBetfairFavourite(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  def receive = {
    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      while (marketStartTime.get.getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        // get exchange favourite
        val exchangeFavourite = betfairServiceNG.getExchangeFavourite(sessionToken, marketId = marketId
        ) map { response =>
          response match {
            case Some(runner) =>
              println("runner = " + runner.selectionId + " " + runner.lastPriceTraded)
              println("back prices = " + runner.ex.get.availableToBack)
              println("lay prices = " + runner.ex.get.availableToLay)
            case _ =>
              println("error no result returned")
          }
        }
        Await.result(exchangeFavourite, 10 seconds)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)

    }
  }

}