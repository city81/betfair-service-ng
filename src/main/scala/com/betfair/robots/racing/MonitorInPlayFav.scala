package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.Breaks.{break, breakable}
import scala.util.{Failure, Success}


class MonitorInPlayFav(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime],
                       backIRList: List[Option[(Double, Double, Double, Int, Int, Double)]])
                      (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring back in running starting - " + marketId)

      var preRace = true

      var favSelectionId = 0L
      var favOdds = 20.0
      var secondFavSelectionId = 0L
      var secondFavOdds = 20.0
      var thirdFavSelectionId = 0L
      var thirdFavOdds = 20.0
      var numberOfRunners = 100

      // monitor market until the off
      while (preRace) {

        betfairServiceNG.listMarketBook(sessionToken, marketIds = Set(marketId)
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {

              if (marketBook.status.equals("OPEN") && marketBook.betDelay.equals(0)) {

                val orderedRunners =
                  marketBook.runners.filter(r => r.status == "ACTIVE").toSeq.sortBy(_.lastPriceTraded)

                favOdds = orderedRunners.head.lastPriceTraded.get
                favSelectionId = orderedRunners.head.selectionId
                secondFavOdds = orderedRunners.drop(1).head.lastPriceTraded.get
                secondFavSelectionId = orderedRunners.drop(1).head.selectionId
                thirdFavOdds = orderedRunners.drop(2).head.lastPriceTraded.get
                thirdFavSelectionId = orderedRunners.drop(2).head.selectionId
                numberOfRunners = orderedRunners.size

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


      var minOdds = (1.01, 1.01)

      // calculate which backIR to use
      breakable {
        for (backIR <- backIRList) {
          if ((favOdds >= backIR.get._2)
            && (favOdds <= backIR.get._3)
            && (numberOfRunners >= backIR.get._4)
            && (numberOfRunners <= backIR.get._5)) {
            minOdds = (backIR.get._6, backIR.get._1)
            println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - matched back IR - " + backIR)
            break
          }
        }
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
                println(new time.DateTime(DateTimeZone.UTC) + " - OPEN - " + marketId + " - market delay - " + marketBook.betDelay)
                if (marketBook.betDelay.equals(0)) {
                  println(new time.DateTime(DateTimeZone.UTC) + " - NON RUNNER - " + marketId + " - market delay - " + marketBook.betDelay)
                  nonRunner = true
                } else {
                  suspended = false
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


      // check if race is backable in running
      if (minOdds._1 > 1.01) {

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
                    if (runner.lastPriceTraded.get < minOdds._2) {
                      inPlay = false
                    }

                    if (inPlay && (runner.lastPriceTraded.get < minOdds._1) && !backedRunners.contains(runner.selectionId)) {

                      // place bet
                      backedRunners += runner.selectionId

                      // place back bet
                      var placeInstructions = Set(
                        PlaceInstruction(
                          selectionId = runner.selectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 2.00,
                            price = decrementPrice(minOdds._1),
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

      }


      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring back in running ending - " + marketId)

    }

      // kill the actor
      context.stop(self)

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