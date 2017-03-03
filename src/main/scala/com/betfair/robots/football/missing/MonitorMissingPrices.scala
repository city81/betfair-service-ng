package com.betfair.robots.football.missing

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class MonitorMissingPrices(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                           marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  val prices: mutable.Map[Long, ListBuffer[Double]] = mutable.Map.empty

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      var counter: Int = 1
      var matchedCounter: Int = 0

      while (marketStartTime.get.plusMinutes(105).getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        // get exchange favourite
        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 6.0
        ) map { response =>
          response match {
            case Some(runners) =>
              runners foreach { runner =>
                runner.ex match {
                  case Some(ex) =>

                    // add price to set
                    var odds: ListBuffer[Double] = ListBuffer.empty
                    if (prices.contains(runner.selectionId)) {
                      odds = prices.get(runner.selectionId).get
                    } else {
                      prices += runner.selectionId -> odds
                    }
                    odds += ex.availableToBack.seq.head.price

                    var trend = "no"
                    if ((odds.size >= 180) &&
                      (odds.last < odds(odds.size - 90)) &&
                      (odds(odds.size - 90) < odds(odds.size - 180))) {
                      trend = "steam"
                    } else if ((odds.size >= 180) &&
                      (odds.last > odds(odds.size - 90)) &&
                      (odds(odds.size - 90) > odds(odds.size - 180))) {
                      trend = "drift"
                    }

                    println(marketId + " - " + runner.selectionId + " - " + trend)

                    // if three increments equal
                    if ((counter > (matchedCounter + 5))
                      && (!trend.equals("no"))
                      && (odds.last > 2.00)
                      && (odds.last < 4.00)
                      && (ex.availableToLay.seq.head.price ==
                      incrementPrice(incrementPrice(incrementPrice(ex.availableToBack.seq.head.price))))) {

                      val placeInstructions = Set(

                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.LAY,
                          limitOrder = Some(LimitOrder(size = 2.00,
                            price = incrementPrice(ex.availableToBack.seq.head.price)))),

                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 2.00,
                            price = incrementPrice(incrementPrice(ex.availableToBack.seq.head.price)))))
                      )

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructions) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>

                          placeExecutionReportContainer.result.instructionReports foreach {
                            instructionReport =>
                              system.scheduler.scheduleOnce(
                                Duration(15, TimeUnit.SECONDS),
                                system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                  marketId, instructionReport, trend)),
                                  DateTime.now().toString
                                    + runner.selectionId + instructionReport.instruction.get.side), "")
                          }

                        case _ =>
                          println("error no result returned")
                      }

                      matchedCounter = counter

                      println(marketId + " - " + runner.selectionId + " - bets placed - both sides")

                    }

                    val priceString = new StringBuilder(marketId + " - " + runner.selectionId + " - " + counter + " - ")
                    ex.availableToBack.toSeq.reverse foreach { priceSize =>
                      priceString.append(priceSize.price + "/" + priceSize.size + ",")
                    }
                    ex.availableToLay foreach { priceSize =>
                      priceString.append(priceSize.price + "/" + priceSize.size + ",")
                    }
                    println(priceString)

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

        counter += 1

      }
      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)
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