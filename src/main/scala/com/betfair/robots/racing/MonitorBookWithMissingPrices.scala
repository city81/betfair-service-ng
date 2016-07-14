package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class MonitorBookWithMissingPrices(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                                   marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  def receive = {

    case _ => {

      var counter: Int = 0
      var matchedCounter: Int = 0

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - pre race - " + marketId)

      while (marketStartTime.get.getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 1000.0
        ) map { response =>
          response match {
            case Some(runners) =>

              val book = BigDecimal(runners.foldLeft(0.0)((acc, x) => acc + (1.0 / x.ex.get.availableToBack.toList(0).price))).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble

              if ((book > 1.035) || (book < 1.005)) {

                runners foreach { runner =>

                  if ((runner.ex.get.availableToBack.seq.head.price < 6.0) &&
                    (runner.ex.get.availableToBack.seq.head.price > 2.0)) {

//                    if (runner.ex.get.availableToLay.seq.head.price ==
//                      incrementPrice(incrementPrice(runner.ex.get.availableToBack.seq.head.price))) {

//                      val missingPrice = incrementPrice(runner.ex.get.availableToBack.seq.head.price)

                      val layPrice = if (book > 1.035) {
                        incrementPrice(runner.ex.get.availableToBack.seq.head.price)
                      } else {
                        decrementPrice(runner.ex.get.availableToBack.seq.head.price)
                      }

                      val backPrice = if (book > 1.035) {
                        incrementPrice(incrementPrice(runner.ex.get.availableToBack.seq.head.price))
                      } else {
                        runner.ex.get.availableToBack.seq.head.price
                      }

                      val stake = 2.00

                      val placeInstructions = Set(

                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.LAY,
                          limitOrder = Some(LimitOrder(size = stake,
                            price = layPrice))),

                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = stake,
                            price = backPrice)))
                      )

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructions) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>

                          println(marketId + " - " + runner.selectionId + " - bets placed")

                          placeExecutionReportContainer.result.instructionReports foreach {
                            instructionReport =>
                              system.scheduler.scheduleOnce(
                                Duration(5, TimeUnit.SECONDS),
                                system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                  marketId, instructionReport, false)),
                                  DateTime.now().toString
                                    + runner.selectionId + instructionReport.instruction.get.side), "")
                          }

                        case _ =>
                          println("error no result returned")
                      }

//                    }

                  }

                }

                matchedCounter = counter

                println(marketId + " - " + book)

              }

              counter += 1

            case _ =>
              println("error no result returned")
          }

        }
        Await.result(priceBoundRunners, 10 seconds)

        Thread.sleep(1000)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - pre race - " + marketId)

    }

  }

  def incrementPrice(price: Double): Double = {

    var newPrice: Double = 0.0

    if (price >= 100.0) {
      newPrice = price + 10.0
    }
    else if (price >= 50.0) {
      newPrice = price + 5.0
    }
    else if (price >= 30.0) {
      newPrice = price + 2.0
    }
    else if (price >= 20.0) {
      newPrice = price + 1.0
    }
    else if (price >= 10.0) {
      newPrice = price + 0.5
    }
    else if (price >= 6.0) {
      newPrice = price + 0.2
    }
    else if (price >= 4.0) {
      newPrice = price + 0.1
    }
    else if (price >= 3.0) {
      newPrice = price + 0.05
    }
    else if (price >= 2.0) {
      newPrice = price + 0.02
    }
    else {
      newPrice = price + 0.01
    }
    BigDecimal(newPrice).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def minWOM1(price: Double): Double = {
    3000.0 * (1.0 / price)
  }

  def minWOM2(price: Double): Double = {
    1500.0 * (1.0 / price)
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