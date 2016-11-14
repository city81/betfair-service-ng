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

class MonitorWOMRunners(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                        marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      while (marketStartTime.get.getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        // get exchange favourite
        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 2.00, higherPrice = 8.0
        ) map { response =>
          response match {
            case Some(runners) =>
              runners foreach { runner =>
                runner.ex match {
                  case Some(ex) =>

                    //                    println(ex)

                    //                    println(runner.selectionId + " - " + ex.availableToLay.seq.head.price + " - " +
                    //                      ex.availableToBack.seq.head.price + " - " + decrementPrice(ex.availableToLay.seq.head.price))

                    if ((decrementPrice(ex.availableToLay.seq.head.price).equals(ex.availableToBack.seq.head.price)) &&
                      (decrementPrice(ex.availableToBack.seq.head.price).equals(ex.availableToBack.seq.drop(1).head.price))) {

                      //                      println(ex.availableToBack.seq.drop(1).head.size + " - "
                      //                        + maxSize(ex.availableToBack.seq.drop(1).head.price) + " - "
                      //                        + (ex.availableToBack.seq.drop(1).head.size > maxSize(ex.availableToBack.seq.drop(1).head.price))
                      //                      )
                      //
                      //                      println(ex.availableToBack.seq.head.size + " - "
                      //                        + minSize(ex.availableToBack.seq.head.price) + " - "
                      //                        + (ex.availableToBack.seq.head.size < minSize(ex.availableToBack.seq.head.price))
                      //                      )
                      //
                      //                      println(ex.availableToLay.seq.head.size + " - "
                      //                        + minSize(ex.availableToLay.seq.head.price) + " - "
                      //                        + (ex.availableToLay.seq.head.size < minSize(ex.availableToLay.seq.head.price))
                      //                      )
                      //
                      //                      println(ex.availableToLay.seq.drop(1).head.size + " - "
                      //                        + maxSize(ex.availableToLay.seq.drop(1).head.price) + " - "
                      //                        + (ex.availableToLay.seq.drop(1).head.size > maxSize(ex.availableToLay.seq.drop(1).head.price))
                      //                      )

//                      println(
//                        (ex.availableToBack.seq.drop(1).head.size > maxSize(ex.availableToBack.seq.drop(1).head.price))
//                          + " - "
//                          + (ex.availableToBack.seq.head.size < minSize(ex.availableToBack.seq.head.price))
//                          + " - "
//                          + (ex.availableToLay.seq.head.size < minSize(ex.availableToLay.seq.head.price))
//                          + " - "
//                          + (ex.availableToLay.seq.drop(1).head.size > maxSize(ex.availableToLay.seq.drop(1).head.price))
//                      )
//
//                      println(
//                        (ex.availableToBack.seq.drop(2).head.size > maxSize(ex.availableToBack.seq.drop(2).head.price))
//                          + " - "
//                          + (ex.availableToBack.seq.drop(1).head.size < minSize(ex.availableToBack.seq.drop(1).head.price))
//                          + " - "
//                          + (ex.availableToBack.seq.head.size < minSize(ex.availableToBack.seq.head.price))
//                          + " - "
//                          + (ex.availableToLay.seq.head.size > maxSize(ex.availableToLay.seq.head.price))
//                      )

                      if ((ex.availableToBack.seq.head.size < minSize(ex.availableToBack.seq.head.price)) &&
                        (ex.availableToLay.seq.head.size < minSize(ex.availableToLay.seq.head.price)) &&
                        (ex.availableToBack.seq.drop(1).head.size > maxSize(ex.availableToBack.seq.drop(1).head.price)) &&
                        (ex.availableToLay.seq.drop(1).head.size > maxSize(ex.availableToLay.seq.drop(1).head.price))) {

                        val layStake = BigDecimal(stake(ex.availableToBack.seq.head.price) / (ex.availableToBack.seq.head.price / ex.availableToLay.seq.head.price)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

                        val placeInstructions = Set(

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.LAY,
                            limitOrder = Some(LimitOrder(size = layStake,
                              price = ex.availableToBack.seq.head.price,
                              persistenceType = PersistenceType.PERSIST))),

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.BACK,
                            limitOrder = Some(LimitOrder(size = stake(ex.availableToBack.seq.head.price),
                              price = ex.availableToLay.seq.head.price,
                              persistenceType = PersistenceType.PERSIST)))
                        )

                        betfairServiceNG.placeOrders(sessionToken,
                          marketId = marketId,
                          instructions = placeInstructions) onComplete {
                          case Success(Some(placeExecutionReportContainer)) =>

                            println(marketId + " - " + runner.selectionId + " - bets placed - wom")

                            placeExecutionReportContainer.result.instructionReports foreach {
                              instructionReport =>
                                system.scheduler.scheduleOnce(
                                  Duration(10, TimeUnit.SECONDS),
                                  system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                    marketId, instructionReport, false)),
                                    DateTime.now().toString
                                      + runner.selectionId + instructionReport.instruction.get.side), "")
                            }

                            println(marketId + " - " + runner.selectionId + " - " + ex)

                          case _ =>
                            println("error no result returned")
                        }

                        Thread.sleep(2000)

                      } else if ((ex.availableToBack.seq.drop(1).head.size < minSize(ex.availableToBack.seq.drop(1).head.price)) &&
                        (ex.availableToBack.seq.head.size < minSize(ex.availableToBack.seq.head.price)) &&
                        (ex.availableToBack.seq.drop(2).head.size > maxSize(ex.availableToBack.seq.drop(2).head.price)) &&
                        (ex.availableToLay.seq.head.size > maxSize(ex.availableToLay.seq.head.price))) {

                        val layStake = BigDecimal(stake(ex.availableToBack.seq.drop(1).head.price) / (ex.availableToBack.seq.drop(1).head.price / ex.availableToBack.seq.head.price)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

                        val placeInstructions = Set(

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.LAY,
                            limitOrder = Some(LimitOrder(size = layStake,
                              price = ex.availableToBack.seq.drop(1).head.price,
                              persistenceType = PersistenceType.PERSIST))),

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.BACK,
                            limitOrder = Some(LimitOrder(size = stake(ex.availableToBack.seq.drop(1).head.price),
                              price = ex.availableToBack.seq.head.price,
                              persistenceType = PersistenceType.PERSIST)))
                        )

                        betfairServiceNG.placeOrders(sessionToken,
                          marketId = marketId,
                          instructions = placeInstructions) onComplete {
                          case Success(Some(placeExecutionReportContainer)) =>

                            println(marketId + " - " + runner.selectionId + " - bets placed - wom - lower back")

                            placeExecutionReportContainer.result.instructionReports foreach {
                              instructionReport =>
                                system.scheduler.scheduleOnce(
                                  Duration(10, TimeUnit.SECONDS),
                                  system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                    marketId, instructionReport, false)),
                                    DateTime.now().toString
                                      + runner.selectionId + instructionReport.instruction.get.side), "")
                            }

                            println(marketId + " - " + runner.selectionId + " - " + ex)

                          case _ =>
                            println("error no result returned")
                        }

                        Thread.sleep(2000)

                      } else if ((ex.availableToLay.seq.drop(1).head.size < minSize(ex.availableToLay.seq.drop(1).head.price)) &&
                        (ex.availableToLay.seq.head.size < minSize(ex.availableToLay.seq.head.price)) &&
                        (ex.availableToLay.seq.drop(2).head.size > maxSize(ex.availableToLay.seq.drop(2).head.price)) &&
                        (ex.availableToBack.seq.head.size > maxSize(ex.availableToBack.seq.head.price))) {

                        val layStake = BigDecimal(stake(ex.availableToLay.seq.head.price) / (ex.availableToLay.seq.head.price / ex.availableToLay.seq.drop(1).head.price)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

                        val placeInstructions = Set(

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.LAY,
                            limitOrder = Some(LimitOrder(size = layStake,
                              price = ex.availableToLay.seq.head.price,
                              persistenceType = PersistenceType.PERSIST))),

                          PlaceInstruction(
                            selectionId = runner.selectionId,
                            side = Side.BACK,
                            limitOrder = Some(LimitOrder(size = stake(ex.availableToLay.seq.head.price),
                              price = ex.availableToLay.seq.drop(1).head.price,
                              persistenceType = PersistenceType.PERSIST)))
                        )

                        betfairServiceNG.placeOrders(sessionToken,
                          marketId = marketId,
                          instructions = placeInstructions) onComplete {
                          case Success(Some(placeExecutionReportContainer)) =>

                            println(marketId + " - " + runner.selectionId + " - bets placed - wom - higher back")

                            placeExecutionReportContainer.result.instructionReports foreach {
                              instructionReport =>
                                system.scheduler.scheduleOnce(
                                  Duration(10, TimeUnit.SECONDS),
                                  system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                    marketId, instructionReport, false)),
                                    DateTime.now().toString
                                      + runner.selectionId + instructionReport.instruction.get.side), "")
                            }

                            println(marketId + " - " + runner.selectionId + " - " + ex)

                          case _ =>
                            println("error no result returned")
                        }

                        Thread.sleep(2000)

                      }


                    }

                  case _ =>
                    println("error no result returned")
                }
              }
            case _ =>
              println("error no result returned")
          }
        }
        Await.result(priceBoundRunners, 10 seconds)

        Thread.sleep(1000)
      }
      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)
    }

  }

  def stake(price: Double): Double = {
    BigDecimal(14.0 / (price - 1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def minSize(price: Double): Double = {
    ((1.0 / price) * 400.0)
  }

  def maxSize(price: Double): Double = {
    ((1.0 / price) * 1400.0)
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

}