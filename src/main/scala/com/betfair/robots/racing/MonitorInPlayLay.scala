package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorInPlayLay(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])
                      (implicit executionContext: ExecutionContext) extends Actor {


  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring in play starting - " + marketId)

      var preRace = true

      // monitor market until the off
      while (preRace) {
        betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 1000.0
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {

              if (marketBook.status.equals("OPEN") && marketBook.betDelay.equals(0)) {

                val orderedRunners =
                  marketBook.runners.filter(r => r.status == "ACTIVE").toSeq.sortBy(_.lastPriceTraded)

                val sum = marketBook.runners.filter(x => x.ex.get.availableToBack.nonEmpty).map(x => 1.0 / x.ex.get.availableToBack.head.price).sum

                if (sum > 1.03) {
                  println("big over round)")
                }

                println(new time.DateTime(DateTimeZone.UTC) + " - OPEN PRE RACE - "
                  + marketId + " - market delay - " + marketBook.betDelay)

              } else {

                preRace = false

              }
            }
          case Success(None) =>
            println("error no result returned")
          case Failure(error) =>
            println("error " + error)
        }

        Thread.sleep(150)

      }

      var inPlay = true

      var suspended = true

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
                  inPlay = false
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

      Thread.sleep(500)

      var count = 1

      while (inPlay) {

        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 1000.0
        ) map { response =>
          response match {
            case Some(runners) =>

              if (runners.isEmpty) {

                inPlay = false

              } else {

//                runners.filter(x => x.ex.get.availableToBack.nonEmpty).filter(x => x.ex.get.availableToLay.nonEmpty) foreach (x => println(marketId + "-->" + count + "-->" + x.selectionId + "-->" + x.ex.get.availableToBack.head.price + "-->" + x.ex.get.availableToLay.head.price))
//
//                if (runners.filter(x => x.ex.get.availableToLay.nonEmpty).filter(x => x.ex.get.availableToLay.head.price < 2.0).size >= 2) println("***** lay 2")
//                if (runners.filter(x => x.ex.get.availableToLay.nonEmpty).filter(x => x.ex.get.availableToLay.head.price < 3.0).size >= 3) println("***** lay 3")
//                if (runners.filter(x => x.ex.get.availableToLay.nonEmpty).filter(x => x.ex.get.availableToLay.head.price < 4.0).size >= 4) println("***** lay 4")
//                if (runners.filter(x => x.ex.get.availableToLay.nonEmpty).filter(x => x.ex.get.availableToLay.head.price < 5.0).size >= 5) println("***** lay 5")
//
//                val sum = runners.filter(x => x.ex.get.availableToBack.nonEmpty).map(x => 1.0 / x.ex.get.availableToBack.head.price).sum
//                println(sum)
//
//                if (sum < 1.00) {
//                  println("***** under round - prices will shorten")
//                  if (sum < 0.9) {
//
//                    val orderedRunners =
//                      runners.toSeq.sortBy(_.ex.get.availableToBack.head.price)
//
//                    val favOdds = orderedRunners.head.ex.get.availableToBack.head.price
//
//                    if ((favOdds > 2.00) && (favOdds < 6.00)) {
//                      val favSelectionId = orderedRunners.head.selectionId
//                      val layOdds = decrementPrice(decrementPrice(favOdds))
//
//                      val placeInstructionsFav = Set(
//                        PlaceInstruction(
//                          selectionId = favSelectionId,
//                          side = Side.BACK,
//                          limitOrder = Some(LimitOrder(size = 2.00,
//                            price = favOdds,
//                            persistenceType = PersistenceType.PERSIST))),
//                        PlaceInstruction(
//                          selectionId = favSelectionId,
//                          side = Side.LAY,
//                          limitOrder = Some(LimitOrder(size = BigDecimal(2.00 * (favOdds / layOdds)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble,
//                            price = layOdds,
//                            persistenceType = PersistenceType.PERSIST)))
//                      )
//
//                      betfairServiceNG.placeOrders(sessionToken,
//                        marketId = marketId,
//                        instructions = placeInstructionsFav) onComplete {
//                        case Success(Some(placeExecutionReportContainer)) =>
//                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + favSelectionId + " - bet fav win in play")
//                        case _ =>
//                          println("error no result returned")
//                      }
//
//                    }
//                    println("***** bet opportunity")
//                  }
//                } else if (sum > 1.25) {
//                  println("***** big over round - prices will drift")
//                  if (runners.filter(x => x.ex.get.availableToBack.nonEmpty).filter(x => x.ex.get.availableToBack.head.price <= 6.0).filter(x => x.ex.get.availableToBack.head.price >= 2.0).size >= 2) {
//                    println("***** bet opportunity")
//                  }
//                }
//
//                count = count + 1
              }

            case _ =>
              println("error no result returned")
              inPlay = false

          }

        }

        Await.result(priceBoundRunners, 10 seconds)
        Thread.sleep(150)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring in play ending - " + marketId)


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