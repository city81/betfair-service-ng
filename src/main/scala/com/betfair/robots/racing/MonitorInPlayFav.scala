package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.robots.{Direction, PriceFormat}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorInPlayFav(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])
                      (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      var favSelectionId = 0L
      var favOdds = 20.0
      var preRace = true

      // monitor market until the off
      while (preRace) {

        betfairServiceNG.listMarketBook(sessionToken, marketIds = Set(marketId)
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {

              if (marketBook.status.equals("OPEN") && marketBook.betDelay.equals(0)) {

                marketBook.runners foreach { runner =>

                  if ((runner.lastPriceTraded.isDefined) && (runner.lastPriceTraded.get < favOdds)) {
                    favOdds = runner.lastPriceTraded.get
                    favSelectionId = runner.selectionId
                  }
                }

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

      val inPlayOdds = BigDecimal(((favOdds - 1.0) * 0.8) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      val inPlayOddsFormatted = PriceFormat.round(Direction.Up, inPlayOdds)

      println(favOdds + " - " + favSelectionId + " - " + inPlayOddsFormatted)

//      if (inPlayOddsFormatted >= 2.0) {

        var inPlay = true

        // monitor market in play
        while (inPlay) {

          // get price bound runners
          val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
            lowerPrice = 1.00, higherPrice = 20.0
          ) map { response =>
            response match {
              case Some(runners) =>

                runners foreach { runner =>

                  if (runner.selectionId.equals(favSelectionId)) {

                    if (runner.lastPriceTraded.get < inPlayOddsFormatted) {
//                      if (runner.lastPriceTraded.get < favOdds) {

                      val placeInstructions = Set(

                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = stake(inPlayOddsFormatted),
                            price = inPlayOddsFormatted,
                            persistenceType = PersistenceType.PERSIST)))
                      )

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructions) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(marketId + " - " + runner.selectionId + " - bet placed")
                        case _ =>
                          println("error no result returned")
                      }

                      inPlay = false

                    }

                  }

                }

              case _ =>
                println("error no result returned")
                inPlay = false
            }
          }
          Await.result(priceBoundRunners, 10 seconds)
          Thread.sleep(500)

        }

//      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)
    }

  }

  def stake(price: Double): Double = {
    val amount = BigDecimal(10.0 / (price - 1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    if (amount > 4.50) 4.50 else amount
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