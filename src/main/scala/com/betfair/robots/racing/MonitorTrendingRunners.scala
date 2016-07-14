package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor}
import com.betfair.domain.{LimitOrder, Side, PlaceInstruction}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class MonitorTrendingRunners(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                             marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  val prices: mutable.Map[Long, ListBuffer[Double]] = mutable.Map.empty

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      var counter: Int = 1

      while (marketStartTime.get.getMillis > (new time.DateTime(DateTimeZone.UTC)).getMillis) {

        // get exchange favourite
        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 3.00, higherPrice = 8.0
        ) map { response =>
          response match {
            case Some(runners) =>

              runners foreach { runner =>
                runner.ex match {
                  case Some(ex) =>

                    val priceString = new StringBuilder(marketId + " - " + runner.selectionId + " - " + counter + " - ")
                    ex.availableToBack.toSeq.reverse foreach { priceSize =>
                      priceString.append(priceSize.price + "/" + priceSize.size + ",")
                    }
                    ex.availableToLay foreach { priceSize =>
                      priceString.append(priceSize.price + "/" + priceSize.size + ",")
                    }
                    println(priceString)

                    // add price to set
                    var odds: ListBuffer[Double] = ListBuffer.empty
                    if (prices.contains(runner.selectionId)) {
                      odds = prices.get(runner.selectionId).get
                    } else {
                      prices += runner.selectionId -> odds
                    }
                    odds += ex.availableToBack.seq.head.price

                    if ((odds.size >= 5) &&
                      (odds.last < odds(odds.size - 3)) &&
                      (odds(odds.size - 3) < odds(odds.size - 5))) {

                      val layPrice = incrementPrice(ex.availableToBack.seq.head.price)
                      val backPrice = incrementPrice(layPrice)
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

                          println(marketId + " - " + runner.selectionId + " - bets placed - steaming")

                          placeExecutionReportContainer.result.instructionReports foreach {
                            instructionReport =>
                              system.scheduler.scheduleOnce(
                                Duration(10, TimeUnit.SECONDS),
                                system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                  marketId, instructionReport, false)),
                                  DateTime.now().toString
                                    + runner.selectionId + instructionReport.instruction.get.side), "")
                          }

                        case _ =>
                          println("error no result returned")
                      }


                      println(marketId + " - " + runner.selectionId + " - trending - steaming")

                    } else if ((odds.size >= 5) &&
                      (odds.last > odds(odds.size - 3)) &&
                      (odds(odds.size - 3) > odds(odds.size - 5))) {

                      val backPrice = decrementPrice(ex.availableToLay.seq.head.price)
                      val layPrice = decrementPrice(backPrice)
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

                          println(marketId + " - " + runner.selectionId + " - bets placed - drifting")

                          placeExecutionReportContainer.result.instructionReports foreach {
                            instructionReport =>
                              system.scheduler.scheduleOnce(
                                Duration(10, TimeUnit.SECONDS),
                                system.actorOf(Props(new ReplaceBet(betfairServiceNG, sessionToken,
                                  marketId, instructionReport, false)),
                                  DateTime.now().toString
                                    + runner.selectionId + instructionReport.instruction.get.side), "")
                          }

                        case _ =>
                          println("error no result returned")
                      }

                      println(marketId + " - " + runner.selectionId + " - trending - drifting")

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