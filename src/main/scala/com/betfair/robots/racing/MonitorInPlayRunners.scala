package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain._
import com.betfair.robots.{Direction, PriceFormat}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class MonitorInPlayRunners(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                           marketId: String, marketStartTime: Option[DateTime],
                           historicalPricesMap: Map[String, Double])
                          (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      // get price bound runners
      val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
        lowerPrice = 1.00, higherPrice = 10.0
      ) map { response =>
        response match {
          case Some(runners) =>
            runners foreach { runner =>

              if (historicalPricesMap.contains(runner.selectionId.toString)) {

                println((new time.DateTime(DateTimeZone.UTC)) + " - selection - " + runner.selectionId.toString
                  + " - percent - " + historicalPricesMap.get(runner.selectionId.toString).get)

                runner.ex match {

                  case Some(ex) =>

                    val layPrice = BigDecimal(((ex.availableToBack.seq.head.price - 1.0) *
                      historicalPricesMap.get(runner.selectionId.toString).get) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                    val layPriceFormatted = PriceFormat.round(Direction.Up, layPrice)
                    val layStake = BigDecimal(2.00 / (layPriceFormatted / ex.availableToBack.seq.head.price)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

                    if (layPriceFormatted < ex.availableToBack.seq.head.price) {

                      val placeInstructions = Set(

                        PlaceInstruction(
                          orderType = OrderType.LIMIT,
                          selectionId = runner.selectionId,
                          side = Side.LAY,
                          limitOrder = Some(LimitOrder(size = layStake,
                            price = layPriceFormatted,
                            persistenceType = PersistenceType.PERSIST))),

                        PlaceInstruction(
                          orderType = OrderType.LIMIT,
                          selectionId = runner.selectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 2.00,
                            price = decrementPrice(ex.availableToBack.seq.head.price),
                            persistenceType = PersistenceType.PERSIST)))
                      )

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructions) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(marketId + " - " + runner.selectionId + " - bets placed")
                        case _ =>
                          println("error no result returned")
                      }

                    }

                  case _ =>
                    println("error no result returned")
                }

              }

            }

          case _ =>
            println("error no result returned")
        }
      }
      Await.result(priceBoundRunners, 10 seconds)

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