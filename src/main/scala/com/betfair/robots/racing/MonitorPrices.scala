package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable
import scala.concurrent._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorPrices(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                    marketId: String, marketStartTime: Option[DateTime])
                   (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring prices starting - " + marketId)

      var preRace = true

      var count = 1

      // monitor market until the off
      while (count < 600) {

        betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 12.0
        ) onComplete {
          case Success(Some(runners)) if (runners.size > 0) =>

                for (runner <- runners) {

                  println((new time.DateTime(DateTimeZone.UTC))
                    + "," + marketId
                    + "," + runner.selectionId
                    + "," + count
                    + "," + runner.ex.get.availableToBack.toList.reverse.mkString
                    + "," + runner.ex.get.availableToLay.mkString)

                }

                count += 1

          case _ =>
            preRace = false
        }

        Thread.sleep(500)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring prices ending - " + marketId)

    }

  }

}