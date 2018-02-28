package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Success}

class MonitorInPlayLay(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                       marketId: String, marketStartTime: Option[DateTime])
                      (implicit executionContext: ExecutionContext) extends Actor {


  def receive = {

    case _ => {

      var sum = 0.0

      var preRace = true

      // monitor market until the off
      while (preRace) {

        try {

          // calculate the book
          betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
            lowerPrice = 1.00, higherPrice = 1000.0) onComplete {
            case Success(Some(runners)) =>

              if (!runners.isEmpty) {

                sum = 0.0

                runners foreach { runner =>
                  //                  println(marketId + " " + runner.ex.get.availableToBack.head.price)
                  sum = sum + (1.0 / runner.ex.get.availableToBack.head.price)
                }

                println(marketId + " book: " + BigDecimal(sum).setScale(2, RoundingMode.HALF_UP))

                if (sum > 1.05) {

                  println(marketId + " book: " + BigDecimal(sum).setScale(2, RoundingMode.HALF_UP) + " big over round")

                  //                  runners foreach { runner =>
                  //                    println(runner.ex.get.availableToBack.head.price)
                  //                  }

                  val orderedRunners = runners.toList.sortWith(_.ex.get.availableToBack.head.price < _.ex.get.availableToBack.head.price)

                  val favSelectionId = orderedRunners.head.selectionId

                  val favLayOdds = incrementPrice(orderedRunners.head.ex.get.availableToBack.head.price)

                  if ((favLayOdds >= 2.0) && (favLayOdds <= 5.0)) {

                    val favLayStake = BigDecimal(12.0 / favLayOdds).setScale(2, RoundingMode.HALF_UP).toDouble

                    var favBackOdds = favLayOdds
                    while (((1.0 / favLayOdds) - (1.0 / favBackOdds)) < 0.01) {
                      favBackOdds = incrementPrice(favBackOdds)
                    }
                    favBackOdds = decrementPrice(favBackOdds)
                    val favBackStake = BigDecimal((favLayOdds / favBackOdds) * favLayStake).setScale(2, RoundingMode.HALF_UP).toDouble

                    // place lay bet
                    //                    println(favSelectionId)
                    //                    println(favLayOdds)
                    //                    println(favLayStake)
                    //                    println(favBackOdds)
                    //                    println(favBackStake)

                    var placeInstructionsFav = Set(
                      PlaceInstruction(
                        selectionId = favSelectionId,
                        side = Side.LAY,
                        limitOrder = Some(LimitOrder(size = favLayStake,
                          price = favLayOdds,
                          persistenceType = PersistenceType.PERSIST))))

                    var placeOrders = betfairServiceNG.placeOrders(sessionToken,
                      marketId = marketId,
                      instructions = placeInstructionsFav
                    ) map { response =>
                      response match {
                        case Some(report) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + favSelectionId + " - lay fav")
                        case _ =>
                          println("error no result returned")
                      }
                    }
                    Await.result(placeOrders, 10 seconds)


                    // wait until it is matched
                    var unmatched = true

                    while (unmatched) {

                      Thread.sleep(2000)

                      val currentOrders = betfairServiceNG.listCurrentOrders(sessionToken,
                        marketIds = Some(("marketIds" -> Set(marketId)))
                      ) map { response =>
                        response match {
                          case Some(report) =>

                            // check all bet size remaining
                            var sizeRemaining = 0.0
                            report.result.currentOrders foreach { order =>
                              sizeRemaining = sizeRemaining + order.sizeRemaining
                            }
                            if (sizeRemaining == 0) {
                              unmatched = false
                              println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + favSelectionId + " - lay fav matched")
                            }
                          case _ =>
                            println("error no result returned")
                        }
                      }
                      Await.result(currentOrders, 10 seconds)

                    }

                    Thread.sleep(1000)

                    // place back bet
                    placeInstructionsFav = Set(
                      PlaceInstruction(
                        selectionId = favSelectionId,
                        side = Side.BACK,
                        limitOrder = Some(LimitOrder(size = favBackStake.toDouble,
                          price = favBackOdds,
                          persistenceType = PersistenceType.PERSIST))))

                    placeOrders = betfairServiceNG.placeOrders(sessionToken,
                      marketId = marketId,
                      instructions = placeInstructionsFav
                    ) map { response =>
                      response match {
                        case Some(report) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + favSelectionId + " - back fav")
                        case _ =>
                          println("error no result returned")
                      }
                    }
                    Await.result(placeOrders, 10 seconds)


                    // wait until it is matched
                    unmatched = true

                    while (unmatched) {

                      Thread.sleep(2000)

                      val currentOrders = betfairServiceNG.listCurrentOrders(sessionToken,
                        marketIds = Some(("marketIds" -> Set(marketId)))
                      ) map { response =>
                        response match {
                          case Some(report) =>
                            // check all bet size remaining
                            var sizeRemaining = 0.0
                            report.result.currentOrders foreach { order =>
                              sizeRemaining = sizeRemaining + order.sizeRemaining
                            }
                            if (sizeRemaining == 0) {
                              unmatched = false
                              println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + favSelectionId + " - back fav matched")
                            }
                          case _ =>
                            println("error no result returned")
                        }
                      }
                      Await.result(currentOrders, 10 seconds)

                    }

                  }

                }

              }

            case Success(None)
            =>
              println("error no result returned")
            case Failure(error)
            =>
              println("error " + error)

          }

          if (sum > 1.05) {
            Thread.sleep(5000)
          } else if (sum > 1.04) {
            Thread.sleep(10000)
          } else if (sum > 1.03) {
            Thread.sleep(15000)
          } else if (sum > 1.02) {
            Thread.sleep(20000)
          } else if (sum > 1.01) {
            Thread.sleep(25000)
          } else {
            Thread.sleep(30000)
          }

          if ((marketStartTime.get.getMillis - 600000 - (new time.DateTime(DateTimeZone.UTC)).getMillis) < 0) {
            preRace = false
          }


        } catch {
          case _: Throwable => println("Got some other kind of exception")
        } finally {

        }

      }

      println(marketId + " monitoring over ten before off")

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
