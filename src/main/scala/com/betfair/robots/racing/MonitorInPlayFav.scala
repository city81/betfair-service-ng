package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorInPlayFav(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])
                      (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring fav starting - " + marketId)

      var preRace = true

      // monitor market until the off
      while (preRace) {

        betfairServiceNG.listMarketBook(sessionToken, marketIds = Set(marketId)
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {

              if (marketBook.status.equals("OPEN") && marketBook.betDelay.equals(0)) {

              } else {

                preRace = false

              }
            }
          case Success(None) =>
            println("error no result returned")
          case Failure(error) =>
            println("error " + error)
        }

        Thread.sleep(500)

      }

      var suspended = true
      var nonRunner = false

      // monitor market until no longer suspended
      while (suspended) {

        betfairServiceNG.listMarketBook(sessionToken, marketIds = Set(marketId)
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {
              if (marketBook.status.equals("OPEN")) {
                suspended = false
                println(new time.DateTime(DateTimeZone.UTC) + " - OPEN - "
                  + marketId + " - market delay - " + marketBook.betDelay)
                if (marketBook.betDelay.equals(0)) {
                  println(new time.DateTime(DateTimeZone.UTC) + " - NON RUNNER - "
                    + marketId + " - market delay - " + marketBook.betDelay)
                  nonRunner = true
                }
              } else {
                println(new time.DateTime(DateTimeZone.UTC) + " - SUSPENDED - "
                  + marketId + " - market delay - " + marketBook.betDelay)
              }
            }
          case Success(None) =>
            println("error no result returned")
          case Failure(error) =>
            println("error " + error)
        }

        Thread.sleep(75)

      }

      var inPlay = true

      var backedRunners = new ListBuffer[Long]()

      while (inPlay) {

        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 10.0
        ) map { response =>
          response match {
            case Some(runners) =>

              if (runners.isEmpty) {

                inPlay = false

              } else {

                runners foreach { runner =>

                  // betting logic here
                  if ((runner.lastPriceTraded.get <= 2.0) && !backedRunners.contains(runner.selectionId)) {

                    // place bet
                    backedRunners += runner.selectionId

                    // place back bet
                    var placeInstructions = Set(
                      PlaceInstruction(
                        selectionId = runner.selectionId,
                        side = Side.BACK,
                        limitOrder = Some(LimitOrder(size = 2.00,
                          price = 2.0,
                          persistenceType = PersistenceType.PERSIST))))

                    var placeOrders = betfairServiceNG.placeOrders(sessionToken,
                      marketId = marketId,
                      instructions = placeInstructions
                    ) map { response =>
                      response match {
                        case Some(report) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - backed")
                        case _ =>
                          println("error no result returned")
                      }
                    }
                    Await.result(placeOrders, 10 seconds)

                  }

                }

              }

            case _ =>
              println("error no result returned")
              inPlay = false
          }
        }
        Await.result(priceBoundRunners, 10 seconds)
        Thread.sleep(250)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)

    }

  }

  def decrementPrice(price: Double): Double = {

    var newPrice: Double = 0.0

    if (price <= 2.0) {
      newPrice = price - 0.01
    }
    else if (price <= 3.0) {
      newPrice = price - 0.02
    }
    else if (price <= 4.0) {
      newPrice = price - 0.05
    }
    else if (price <= 6.0) {
      newPrice = price - 0.1
    }
    else if (price <= 10.0) {
      newPrice = price - 0.2
    }
    else if (price <= 20.0) {
      newPrice = price - 0.5
    }
    else if (price <= 30.0) {
      newPrice = price - 1.0
    }
    else if (price <= 50.0) {
      newPrice = price - 2.0
    }
    else if (price <= 100.0) {
      newPrice = price - 5.0
    }
    else {
      newPrice = price - 10.0
    }
    BigDecimal(newPrice).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

}