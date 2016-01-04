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

class ReplaceBets(betfairServiceNG: BetfairServiceNG, sessionToken: String, marketId: String,
                  placeInstructionReports: Set[PlaceInstructionReport])(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  def receive = {

    case _ => {

      placeInstructionReports foreach {
        report =>
          val replaceInstructions = Set(
            ReplaceInstruction(
              betId = report.betId.get,
              newPrice = {
                if (report.instruction.get.side.equals(Side.LAY)) {
                  incrementPrice(report.instruction.get.limitOrder.get.price)
                } else {
                  decrementPrice(report.instruction.get.limitOrder.get.price)
                }
              }
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
                      println(marketId + " - " + report.instruction.get.selectionId + " - bet replaced")
                      system.scheduler.scheduleOnce(
                        Duration(15, TimeUnit.SECONDS),
                        system.actorOf(Props(new ReplaceBets(betfairServiceNG, sessionToken, marketId,
                          Set(instructionReport.placeInstructionReport))),
                          DateTime.now().toString), "")
                    case _ =>
                      println(marketId + " - " + report.instruction.get.selectionId + " - bet already matched")
                  }
              }
            case _ =>
              println("error no result returned")
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