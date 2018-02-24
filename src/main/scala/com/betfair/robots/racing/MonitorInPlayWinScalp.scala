package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.robots.{Direction, PriceFormat}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorInPlayWinScalp(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                            marketId: String, marketStartTime: Option[DateTime])
                           (implicit executionContext: ExecutionContext) extends Actor {


  val oddsBackFav = List[(Double, Double, Double, Double, Double, Double, Double)](
    (4.25,5.0,4.75,5.5,5.75,6.5,0.8),
    (4.0,4.75,4.75,5.5,5.25,6.0,0.8),
    (4.0,4.75,4.75,5.5,5.5,6.25,0.8),
    (4.25,5.0,4.75,5.5,5.25,6.0,0.8),
    (4.25,5.0,4.75,5.5,6.0,6.75,0.8),
    (3.75,4.5,4.5,5.25,5.5,6.25,0.8),
    (3.5,4.25,4.5,5.25,5.25,6.0,0.8),
    (4.5,5.25,4.75,5.5,5.75,6.5,0.8),
    (4.0,4.75,4.25,5.0,5.75,6.5,0.8),
    (4.0,4.75,4.5,5.25,6.0,6.75,0.8),
    (3.75,4.5,4.75,5.5,5.5,6.25,0.8),
    (3.5,4.25,4.5,5.25,5.5,6.25,0.8),
    (2.5,3.25,4.25,5.0,4.75,5.5,0.8),
    (4.0,4.75,4.75,5.5,5.75,6.5,0.8),
    (4.0,4.75,4.75,5.5,6.0,6.75,0.8),
    (4.5,5.25,4.75,5.5,5.25,6.0,0.8),
    (3.5,4.25,4.25,5.0,5.5,6.25,0.8),
    (3.75,4.5,4.5,5.25,5.25,6.0,0.8),
    (4.25,5.0,4.5,5.25,5.25,6.0,0.8),
    (4.25,5.0,5.0,5.75,5.25,6.0,0.8),
    (4.25,5.0,5.0,5.75,5.75,6.5,0.8),
    (4.25,5.0,4.75,5.5,5.5,6.25,0.8),
    (3.0,3.75,4.25,5.0,4.75,5.5,0.8),
    (4.0,4.75,4.5,5.25,5.5,6.25,0.8),
    (4.5,5.25,5.0,5.75,5.75,6.5,0.8),
    (3.5,4.25,4.25,5.0,5.25,6.0,0.8),
    (4.0,4.75,4.5,5.25,5.0,5.75,0.8),
    (4.0,4.75,4.5,5.25,5.75,6.5,0.8),
    (4.25,5.0,5.0,5.75,6.0,6.75,0.8),
    (3.75,4.5,4.75,5.5,5.25,6.0,0.8),
    (3.5,4.25,4.5,5.25,5.0,5.75,0.8),
    (4.0,4.75,5.0,5.75,5.25,6.0,0.8),
    (3.5,4.25,4.5,5.25,4.75,5.5,0.8),
    (2.25,3.0,3.0,3.75,5.5,6.25,0.8),
    (3.75,4.5,4.25,5.0,5.5,6.25,0.8),
    (4.0,4.75,5.0,5.75,5.5,6.25,0.8),
    (4.0,4.75,4.5,5.25,4.75,5.5,0.8),
    (4.0,4.75,4.5,5.25,5.25,6.0,0.8),
    (3.5,4.25,4.75,5.5,5.5,6.25,0.8),
    (2.5,3.25,4.25,5.0,5.0,5.75,0.8),
    (2.5,3.25,4.5,5.25,4.75,5.5,0.8),
    (3.75,4.5,4.5,5.25,5.0,5.75,0.8),
    (4.5,5.25,5.0,5.75,6.0,6.75,0.8),
    (3.25,4.0,4.5,5.25,5.25,6.0,0.8),
    (2.75,3.5,4.25,5.0,4.75,5.5,0.8),
    (3.0,3.75,4.25,5.0,5.0,5.75,0.8),
    (3.75,4.5,4.5,5.25,4.75,5.5,0.8),
    (3.75,4.5,4.25,5.0,6.0,6.75,0.8),
    (2.5,3.25,3.0,3.75,5.5,6.25,0.7),
    (2.5,3.25,4.5,5.25,5.25,6.0,0.8),
    (2.5,3.25,3.0,3.75,5.25,6.0,0.8),
    (2.5,3.25,4.25,5.0,5.25,6.0,0.8),
    (4.25,5.0,4.5,5.25,5.5,6.25,0.8),
    (2.25,3.0,4.25,5.0,5.25,6.0,0.8),
    (2.25,3.0,4.25,5.0,5.0,5.75,0.8),
    (2.25,3.0,3.0,3.75,5.25,6.0,0.8),
    (3.0,3.75,4.5,5.25,5.25,6.0,0.8),
    (2.25,3.0,4.5,5.25,5.25,6.0,0.8)
  )

  val oddsBackSecondFav = List[(Double, Double, Double, Double, Double, Double, Double)](
    (3.75,4.5,5.25,6.0,6.25,7.0,0.8),
    (3.75,4.5,5.25,6.0,6.0,6.75,0.8),
    (3.0,3.75,3.75,4.5,5.25,6.0,0.8),
    (3.25,4.0,3.75,4.5,5.25,6.0,0.8),
    (4.0,4.75,5.25,6.0,6.0,6.75,0.8),
    (4.25,5.0,5.25,6.0,6.0,6.75,0.8),
    (2.75,3.5,3.75,4.5,5.25,6.0,0.8),
    (3.0,3.75,3.5,4.25,5.25,6.0,0.8),
    (3.0,3.75,3.75,4.5,4.0,4.75,0.8),
    (3.5,4.25,5.0,5.75,5.25,6.0,0.7),
    (2.75,3.5,3.75,4.5,5.5,6.25,0.8),
    (4.25,5.0,5.5,6.25,6.0,6.75,0.8),
    (2.75,3.5,3.75,4.5,4.0,4.75,0.8),
    (2.75,3.5,3.5,4.25,5.25,6.0,0.8),
    (3.25,4.0,5.0,5.75,5.25,6.0,0.7),
    (3.75,4.5,5.0,5.75,6.25,7.0,0.8),
    (2.5,3.25,4.75,5.5,5.5,6.25,0.8),
    (4.0,4.75,5.0,5.75,6.0,6.75,0.8),
    (3.25,4.0,3.75,4.5,5.0,5.75,0.6),
    (3.0,3.75,3.75,4.5,5.5,6.25,0.8),
    (2.75,3.5,3.5,4.25,5.5,6.25,0.8),
    (3.25,4.0,5.0,5.75,5.5,6.25,0.7),
    (3.25,4.0,3.75,4.5,5.5,6.25,0.8),
    (2.5,3.25,3.5,4.25,5.25,6.0,0.8),
    (3.25,4.0,4.75,5.5,5.5,6.25,0.7)
  )

  val oddsBackThirdFav = List[(Double, Double, Double, Double, Double, Double, Double)](
  )


  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring fav, second and third fav in play starting - " + marketId)

      var favSelectionId = 0L
      var favOdds = 20.0
      var secondFavSelectionId = 0L
      var secondFavOdds = 20.0
      var thirdFavSelectionId = 0L
      var thirdFavOdds = 20.0
      var fourthFavSelectionId = 0L
      var fourthFavOdds = 20.0

      var preRace = true

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
                if (orderedRunners.size > 3) {
                  thirdFavOdds = orderedRunners.drop(2).head.lastPriceTraded.get
                  thirdFavSelectionId = orderedRunners.drop(2).head.selectionId
                  fourthFavOdds = orderedRunners.drop(3).head.lastPriceTraded.get
                  fourthFavSelectionId = orderedRunners.drop(3).head.selectionId
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

      if (favOdds.equals(secondFavOdds) || secondFavOdds.equals(thirdFavOdds) || thirdFavOdds.equals(fourthFavOdds)) {

        println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - no bets placed - same odds")

      } else {

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

        println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring in play - "
          + marketId + " - " + favOdds + " - " + secondFavOdds + " - " + thirdFavOdds
        )

        var oddsBackFavBetPlaced = false
        var oddsBackSecondFavBetPlaced = false
        var oddsBackThirdFavBetPlaced = false

        while (inPlay) {

          val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
            lowerPrice = 1.00, higherPrice = 8.5
          ) map { response =>
            response match {
              case Some(runners) =>

//                println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring prices - " + marketId)

                if (runners.isEmpty) {

                  inPlay = false

                } else {

                  runners foreach { runner =>

                    if (runner.selectionId == favSelectionId) {

                      for (odd <- oddsBackFav) {

                        if ((!oddsBackFavBetPlaced) && (favOdds >= odd._1) && (favOdds <= odd._2)
                          && (secondFavOdds >= odd._3) && (secondFavOdds <= odd._4)
                          && (thirdFavOdds >= odd._5) && (thirdFavOdds <= odd._6)
                        ) {

                          val inPlayFavOdds = BigDecimal(((favOdds - 1.0) * odd._7) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormatted = PriceFormat.round(Direction.Up, inPlayFavOdds)
                          val inPlayFavOddsBet = BigDecimal(((favOdds - 1.0) * (odd._7 - 0.05)) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormattedBet = PriceFormat.round(Direction.Up, inPlayFavOddsBet)

                          if (runner.lastPriceTraded.get <= inPlayFavOddsFormatted) {

                            val placeInstructionsFav = Set(
                              PlaceInstruction(
                                selectionId = favSelectionId,
                                side = Side.BACK,
                                limitOrder = Some(LimitOrder(size = 2.50,
                                  price = inPlayFavOddsFormattedBet,
                                  persistenceType = PersistenceType.PERSIST))))

                            betfairServiceNG.placeOrders(sessionToken,
                              marketId = marketId,
                              instructions = placeInstructionsFav) onComplete {
                              case Success(Some(placeExecutionReportContainer)) =>
                                println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet fav win in play")
                              case _ =>
                                println("error no result returned")
                            }

                            oddsBackFavBetPlaced = true

                          }

                        }

                      }

                    } else if (runner.selectionId == secondFavSelectionId) {

                      for (odd <- oddsBackSecondFav) {

                        if ((!oddsBackSecondFavBetPlaced) && (favOdds >= odd._1) && (favOdds <= odd._2)
                          && (secondFavOdds >= odd._3) && (secondFavOdds <= odd._4)
                          && (thirdFavOdds >= odd._5) && (thirdFavOdds <= odd._6)
                        ) {

                          val inPlayFavOdds = BigDecimal(((secondFavOdds - 1.0) * odd._7) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormatted = PriceFormat.round(Direction.Up, inPlayFavOdds)
                          val inPlayFavOddsBet = BigDecimal(((secondFavOdds - 1.0) * (odd._7 - 0.05)) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormattedBet = PriceFormat.round(Direction.Up, inPlayFavOddsBet)

                          if (runner.lastPriceTraded.get <= inPlayFavOddsFormatted) {

                            val placeInstructionsFav = Set(
                              PlaceInstruction(
                                selectionId = secondFavSelectionId,
                                side = Side.BACK,
                                limitOrder = Some(LimitOrder(size = 2.25,
                                  price = inPlayFavOddsFormattedBet,
                                  persistenceType = PersistenceType.PERSIST))))

                            betfairServiceNG.placeOrders(sessionToken,
                              marketId = marketId,
                              instructions = placeInstructionsFav) onComplete {
                              case Success(Some(placeExecutionReportContainer)) =>
                                println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet second fav win in play")
                              case _ =>
                                println("error no result returned")
                            }

                            oddsBackSecondFavBetPlaced = true

                          }

                        }

                      }

                    } else if (runner.selectionId == thirdFavSelectionId) {

                      for (odd <- oddsBackThirdFav) {

                        if ((!oddsBackThirdFavBetPlaced) && (favOdds >= odd._1) && (favOdds <= odd._2)
                          && (secondFavOdds >= odd._3) && (secondFavOdds <= odd._4)
                          && (thirdFavOdds >= odd._5) && (thirdFavOdds <= odd._6)
                        ) {

                          val inPlayFavOdds = BigDecimal(((thirdFavOdds - 1.0) * odd._7) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormatted = PriceFormat.round(Direction.Up, inPlayFavOdds)
                          val inPlayFavOddsBet = BigDecimal(((thirdFavOdds - 1.0) * (odd._7 - 0.05)) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                          val inPlayFavOddsFormattedBet = PriceFormat.round(Direction.Up, inPlayFavOddsBet)

                          if (runner.lastPriceTraded.get <= inPlayFavOddsFormatted) {

                            val placeInstructionsFav = Set(
                              PlaceInstruction(
                                selectionId = thirdFavSelectionId,
                                side = Side.BACK,
                                limitOrder = Some(LimitOrder(size = 2.00,
                                  price = inPlayFavOddsFormattedBet,
                                  persistenceType = PersistenceType.PERSIST))))

                            betfairServiceNG.placeOrders(sessionToken,
                              marketId = marketId,
                              instructions = placeInstructionsFav) onComplete {
                              case Success(Some(placeExecutionReportContainer)) =>
                                println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet third fav win in play")
                              case _ =>
                                println("error no result returned")
                            }

                            oddsBackThirdFavBetPlaced = true

                          }

                        }

                      }

                    }

                  }

                }

              case _ =>
                println("error no result returned")
                inPlay = false

            }

          }

          Await.result(priceBoundRunners, 10 seconds)
          Thread.sleep(120)

        }

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring fav, second and third fav in play ending - " + marketId)


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