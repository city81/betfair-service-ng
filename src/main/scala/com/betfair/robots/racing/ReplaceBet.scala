package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time.DateTime

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scala.util.Success

class ReplaceBet(betfairServiceNG: BetfairServiceNG,
                 sessionToken: String,
                 marketId: String,
                 placeInstructionReport: PlaceInstructionReport,
                 movedAlready: Boolean)(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  def receive = {

    case _ => {

      // check if price has been matched or has moved against us
      val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
        lowerPrice = 1.00, higherPrice = 16.0
      ) map { response =>
        response match {
          case Some(runners) =>
            runners foreach { runner =>

              // interested runner?
              if (runner.selectionId == placeInstructionReport.instruction.get.selectionId) {

                // bet placed on the lay side
                if (placeInstructionReport.instruction.get.side.equals(Side.LAY)) {

                  // bet moved away too much
                  if (runner.ex.get.availableToBack.tail.head.price >
                    placeInstructionReport.instruction.get.limitOrder.get.price) {

                    var newPrice = incrementPrice(
                      placeInstructionReport.instruction.get.limitOrder.get.price)

                    if (movedAlready) {
                      newPrice = incrementPrice(newPrice)
                    }

                    val replaceInstructions = Set(
                      ReplaceInstruction(
                        betId = placeInstructionReport.betId.get,
                        newPrice = newPrice
                      )
                    )

                    betfairServiceNG.replaceOrders(sessionToken,
                      marketId = marketId,
                      instructions = replaceInstructions) onComplete {
                      case Success(Some(replaceExecutionReportContainer)) =>
                        replaceExecutionReportContainer.result.instructionReports foreach {
                          instructionReport =>
                            instructionReport.placeInstructionReport.status match {
                              case Some(status) if status.equals(InstructionReportStatus.SUCCESS) =>

                                system.scheduler.scheduleOnce(
                                  Duration(5, TimeUnit.SECONDS),
                                  system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                    marketId, instructionReport.placeInstructionReport, true)),
                                    DateTime.now().toString
                                      + runner.selectionId + placeInstructionReport.instruction.get.side), "")

                                println(marketId + " - " + runner.selectionId + " - "
                                  + placeInstructionReport.instruction.get.side + " - bet replaced")
                              case _ =>
                                println(marketId + " - " + runner.selectionId + " - "
                                  + placeInstructionReport.instruction.get.side + " - bet already matched")
                            }
                        }
                      case _ =>
                        println("error no result returned")
                    }

                  } else if (runner.ex.get.availableToLay.head.price >
                    placeInstructionReport.instruction.get.limitOrder.get.price) {

                    // check again

                    system.scheduler.scheduleOnce(
                      Duration(5, TimeUnit.SECONDS),
                      system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                        marketId, placeInstructionReport, false)),
                        DateTime.now().toString
                          + runner.selectionId + placeInstructionReport.instruction.get.side), "")
                    
                    println(marketId + " - " + runner.selectionId + " - "
                      + placeInstructionReport.instruction.get.side + " - bet not matched")

                  } else {
                    // bet has been matched
                    println(marketId + " - " + runner.selectionId + " - "
                      + placeInstructionReport.instruction.get.side + " - bet already matched")
                  }


                } else {

                  // bet moved away too much
                  if (runner.ex.get.availableToLay.tail.head.price <
                    placeInstructionReport.instruction.get.limitOrder.get.price) {

                    var newPrice = decrementPrice(
                      placeInstructionReport.instruction.get.limitOrder.get.price)

                    if (movedAlready) {
                      newPrice = decrementPrice(newPrice)
                    }

                    val replaceInstructions = Set(
                      ReplaceInstruction(
                        betId = placeInstructionReport.betId.get,
                        newPrice = newPrice
                      )
                    )

                    betfairServiceNG.replaceOrders(sessionToken,
                      marketId = marketId,
                      instructions = replaceInstructions) onComplete {
                      case Success(Some(replaceExecutionReportContainer)) =>
                        replaceExecutionReportContainer.result.instructionReports foreach {
                          instructionReport =>
                            instructionReport.placeInstructionReport.status match {
                              case Some(status) if status.equals(InstructionReportStatus.SUCCESS) =>

                                system.scheduler.scheduleOnce(
                                  Duration(5, TimeUnit.SECONDS),
                                  system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                    marketId, instructionReport.placeInstructionReport, true)),
                                    DateTime.now().toString
                                      + runner.selectionId + placeInstructionReport.instruction.get.side), "")

                                println(marketId + " - " + runner.selectionId + " - "
                                  + placeInstructionReport.instruction.get.side + " - bet replaced")
                              case _ =>
                                println(marketId + " - " + runner.selectionId + " - "
                                  + placeInstructionReport.instruction.get.side + " - bet already matched")
                            }
                        }
                      case _ =>
                        println("error no result returned")
                    }

                  } else if (runner.ex.get.availableToBack.head.price <
                    placeInstructionReport.instruction.get.limitOrder.get.price) {

                    // check again

                    system.scheduler.scheduleOnce(
                      Duration(5, TimeUnit.SECONDS),
                      system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                        marketId, placeInstructionReport, false)),
                        DateTime.now().toString
                          + runner.selectionId + placeInstructionReport.instruction.get.side), "")

                    println(marketId + " - " + runner.selectionId + " - "
                      + placeInstructionReport.instruction.get.side + " - bet not matched")

                  } else {
                    // bet has been matched
                    println(marketId + " - " + runner.selectionId + " - "
                      + placeInstructionReport.instruction.get.side + " - bet already matched")
                  }

                }

              }
            }
        }
      }
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