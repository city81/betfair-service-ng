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

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring fav and second fav in play starting - " + marketId)

      var favSelectionId = 0L
      var favOdds = 20.0
      var secondFavSelectionId = 0L
      var secondFavOdds = 20.0
      var thirdFavSelectionId = 0L
      var thirdFavOdds = 20.0

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
                if (orderedRunners.size > 2) {
                  thirdFavOdds = orderedRunners.drop(2).head.lastPriceTraded.get
                  thirdFavSelectionId = orderedRunners.drop(2).head.selectionId
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

        Thread.sleep(250)

      }

      if (favOdds.equals(secondFavOdds) || secondFavOdds.equals(thirdFavOdds)) {

        println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - no bets placed - same odds")

      } else {

        var inPlay = true

        val inPlayFavOdds = BigDecimal(((favOdds - 1.0) * 0.8) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlayFavOddsFormatted = PriceFormat.round(Direction.Up, inPlayFavOdds)
        val inPlayFavOdds2 = BigDecimal(((favOdds - 1.0) * 0.75) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlayFavOddsFormatted2 = PriceFormat.round(Direction.Up, inPlayFavOdds2)

        val inPlaySecondFavOddsa = BigDecimal(((secondFavOdds - 1.0) * 0.8) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormatteda = PriceFormat.round(Direction.Up, inPlaySecondFavOddsa)
        val inPlaySecondFavOdds2a = BigDecimal(((secondFavOdds - 1.0) * 0.75) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormatted2a = PriceFormat.round(Direction.Up, inPlaySecondFavOdds2a)

        val inPlaySecondFavOddsb = BigDecimal(((secondFavOdds - 1.0) * 0.6) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormattedb = PriceFormat.round(Direction.Up, inPlaySecondFavOddsb)
        val inPlaySecondFavOdds2b = BigDecimal(((secondFavOdds - 1.0) * 0.55) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormatted2b = PriceFormat.round(Direction.Up, inPlaySecondFavOdds2b)

        val inPlaySecondFavOddsc = BigDecimal(((secondFavOdds - 1.0) * 0.5) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormattedc = PriceFormat.round(Direction.Up, inPlaySecondFavOddsc)
        val inPlaySecondFavOdds2c = BigDecimal(((secondFavOdds - 1.0) * 0.45) + 1.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        val inPlaySecondFavOddsFormatted2c = PriceFormat.round(Direction.Up, inPlaySecondFavOdds2c)


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


        println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring in play - "
          + marketId
          + " - " + favOdds + " - " + secondFavOdds
        )

        while (inPlay) {

          val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
            lowerPrice = 1.00, higherPrice = 8.0
          ) map { response =>
            response match {
              case Some(runners) =>

                if (runners.isEmpty) {

                  inPlay = false

                } else {

                  runners foreach { runner =>

                    if ((runner.selectionId == favSelectionId) && (favOdds > 2.25) && (favOdds < 4.75) &&
                      (runner.lastPriceTraded.get <= inPlayFavOddsFormatted)) {

                      val placeInstructionsFav = Set(
                        PlaceInstruction(
                          selectionId = favSelectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 3.00,
                            price = inPlayFavOddsFormatted2,
                            persistenceType = PersistenceType.PERSIST))))

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructionsFav) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet fav win in play")
                        case _ =>
                          println("error no result returned")
                      }

                      favOdds = 20.0

                    }

                    if ((runner.selectionId == secondFavSelectionId) && (secondFavOdds > 5.0) && (secondFavOdds < 7.25) &&
                      (runner.lastPriceTraded.get <= inPlaySecondFavOddsFormatteda)) {

                      val placeInstructionsFav = Set(
                        PlaceInstruction(
                          selectionId = secondFavSelectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 3.00,
                            price = inPlaySecondFavOddsFormatted2a,
                            persistenceType = PersistenceType.PERSIST))))

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructionsFav) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet second fav win in play")
                        case _ =>
                          println("error no result returned")
                      }

                      secondFavOdds = 20.0

                    }

                    if ((runner.selectionId == secondFavSelectionId) && (secondFavOdds > 3.5) && (secondFavOdds < 4.5) &&
                      (runner.lastPriceTraded.get <= inPlaySecondFavOddsFormattedb)) {

                      val placeInstructionsFav = Set(
                        PlaceInstruction(
                          selectionId = secondFavSelectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 3.00,
                            price = inPlaySecondFavOddsFormatted2b,
                            persistenceType = PersistenceType.PERSIST))))

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructionsFav) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet second fav win in play")
                        case _ =>
                          println("error no result returned")
                      }

                      secondFavOdds = 20.0

                    }

                    if ((runner.selectionId == secondFavSelectionId) && (secondFavOdds >= 4.5) && (secondFavOdds <= 5.0) &&
                      (runner.lastPriceTraded.get <= inPlaySecondFavOddsFormattedc)) {

                      val placeInstructionsFav = Set(
                        PlaceInstruction(
                          selectionId = secondFavSelectionId,
                          side = Side.BACK,
                          limitOrder = Some(LimitOrder(size = 3.00,
                            price = inPlaySecondFavOddsFormatted2c,
                            persistenceType = PersistenceType.PERSIST))))

                      betfairServiceNG.placeOrders(sessionToken,
                        marketId = marketId,
                        instructions = placeInstructionsFav) onComplete {
                        case Success(Some(placeExecutionReportContainer)) =>
                          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + runner.selectionId + " - bet second fav win in play")
                        case _ =>
                          println("error no result returned")
                      }

                      secondFavOdds = 20.0

                    }

                  }

                }

                if (((favOdds <= 2.25) || (favOdds >= 4.75)) &&
                  ((secondFavOdds <= 3.5) || (secondFavOdds >= 7.25))) {
                  inPlay = false
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

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring fav and second fav in play ending - " + marketId)


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