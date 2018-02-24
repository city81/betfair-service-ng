package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain.{LimitOrder, PersistenceType, PlaceInstruction, Side}
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class MonitorInPlayWinNonHcap(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                              marketId: String, marketStartTime: Option[DateTime])
                             (implicit executionContext: ExecutionContext) extends Actor {

  def receive = {

    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring non hcap fav and second fav starting - " + marketId)

      var favSelectionId = 0L
      var favOdds = 20.0
      var secondFavSelectionId = 0L
      var secondFavOdds = 20.0
      var thirdFavSelectionId = 0L
      var thirdFavOdds = 20.0
      var fourthFavSelectionId = 0L
      var fourthFavOdds = 20.0
      var runners = 0

      var preRace = true

      val oddsLayFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
      )

      val oddsLaySecondFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
//        (2.0, 2.75, 3.5, 4.25, 5.75, 6.5, 5.0, 9.0),
//        (2.0, 2.75, 3.25, 4.0, 5.75, 6.5, 5.0, 9.0),
//        (2.0, 2.75, 3.5, 4.25, 5.5, 6.25, 5.0, 9.0),
//        (2.0, 2.75, 3.25, 4.0, 5.5, 6.25, 5.0, 9.0)
      )

      val oddsLayThirdFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
//        (3.0, 3.75, 3.5, 4.25, 4.25, 5.0, 5.0, 9.0),
//        (3.0, 3.75, 3.5, 4.25, 4.25, 5.0, 6.0, 10.0),
//        (2.5, 3.25, 3.5, 4.25, 5.25, 6.0, 5.0, 9.0),
//        (2.5, 3.25, 3.5, 4.25, 5.25, 6.0, 6.0, 10.0),
//        (3.0, 3.75, 3.5, 4.25, 4.25, 5.0, 7.0, 11.0),
//        (2.5, 3.25, 3.25, 4.0, 5.0, 5.75, 7.0, 11.0),
//        (2.75, 3.5, 3.5, 4.25, 4.25, 5.0, 7.0, 11.0),
//        (2.5, 3.25, 3.5, 4.25, 4.25, 5.0, 7.0, 11.0),
//        (3.0, 3.75, 3.75, 4.5, 4.25, 5.0, 5.0, 9.0),
//        (2.5, 3.25, 3.5, 4.25, 5.5, 6.25, 6.0, 10.0)
      )

      val oddsBackFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
      )

      val oddsBackSecondFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
      )

      val oddsBackThirdFav = List[(Double, Double, Double, Double, Double, Double, Double, Double)](
      )


      // monitor market until the off
      while (preRace) {

        betfairServiceNG.listMarketBook(sessionToken, marketIds = Set(marketId)
        ) onComplete {
          case Success(Some(listMarketBookContainer)) =>
            for (marketBook <- listMarketBookContainer.result) {

              if (marketBook.status.equals("OPEN") && marketBook.betDelay.equals(0)) {

                val orderedRunners =
                  marketBook.runners.filter(r => r.status == "ACTIVE").toSeq.sortBy(_.lastPriceTraded)

                runners = orderedRunners.size

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

        Thread.sleep(100)

      }


      if (favOdds.equals(secondFavOdds) || secondFavOdds.equals(thirdFavOdds) || thirdFavOdds.equals(fourthFavOdds)) {

        println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - no bets placed - same odds")

      } else {

        // build the place instructions array

        var placeInstructions = Map[Long, PlaceInstruction]()

        var oddsLayFavBetPlaced = false

        for (odd <- oddsLayFav) {

          if ((!oddsLayFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            val price = incrementPrice(incrementPrice(favOdds))

            placeInstructions += (favSelectionId ->
              PlaceInstruction(
                selectionId = favSelectionId,
                side = Side.LAY,
                limitOrder = Some(LimitOrder(size = stake(price),
                  price = price,
                  persistenceType = PersistenceType.PERSIST))))

            oddsLayFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - lay fav - " + marketId)

          }

        }

        var oddsLaySecondFavBetPlaced = false

        for (odd <- oddsLaySecondFav) {

          if ((!oddsLaySecondFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            val price = incrementPrice(incrementPrice(secondFavOdds))

            placeInstructions += (secondFavSelectionId ->
              PlaceInstruction(
                selectionId = secondFavSelectionId,
                side = Side.LAY,
                limitOrder = Some(LimitOrder(size = stake(price),
                  price = price,
                  persistenceType = PersistenceType.PERSIST))))

            oddsLaySecondFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - lay second fav - " + marketId)

          }

        }

        var oddsLayThirdFavBetPlaced = false

        for (odd <- oddsLayThirdFav) {

          if ((!oddsLayThirdFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            val price = incrementPrice(incrementPrice(thirdFavOdds))

            placeInstructions += (thirdFavSelectionId ->
              PlaceInstruction(
                selectionId = thirdFavSelectionId,
                side = Side.LAY,
                limitOrder = Some(LimitOrder(size = stake(price),
                  price = price,
                  persistenceType = PersistenceType.PERSIST))))

            oddsLayThirdFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - lay third fav - " + marketId)

          }

        }

        var oddsBackFavBetPlaced = false

        for (odd <- oddsBackFav) {

          if ((!oddsBackFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            if (placeInstructions.contains(favSelectionId)) {
              placeInstructions -= favSelectionId
            } else {

              val price = decrementPrice(decrementPrice(favOdds))

              placeInstructions += (favSelectionId ->
                PlaceInstruction(
                  selectionId = favSelectionId,
                  side = Side.BACK,
                  limitOrder = Some(LimitOrder(size = stake(price),
                    price = price,
                    persistenceType = PersistenceType.PERSIST))))
            }

            oddsBackFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - back fav - " + marketId)

          }

        }

        var oddsBackSecondFavBetPlaced = false

        for (odd <- oddsBackSecondFav) {

          if ((!oddsBackSecondFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            if (placeInstructions.contains(secondFavSelectionId)) {
              placeInstructions -= secondFavSelectionId
            } else {

              val price = decrementPrice(decrementPrice(secondFavOdds))

              placeInstructions += (secondFavSelectionId ->
                PlaceInstruction(
                  selectionId = secondFavSelectionId,
                  side = Side.BACK,
                  limitOrder = Some(LimitOrder(size = stake(price),
                    price = price,
                    persistenceType = PersistenceType.PERSIST))))
            }

            oddsBackSecondFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - back second fav - " + marketId)


          }

        }

        var oddsBackThirdFavBetPlaced = false

        for (odd <- oddsBackThirdFav) {

          if ((!oddsBackThirdFavBetPlaced) && (favOdds > odd._1) && (favOdds < odd._2)
            && (secondFavOdds > odd._3) && (secondFavOdds < odd._4)
            && (thirdFavOdds > odd._5) && (thirdFavOdds < odd._6)
            && (runners >= odd._7) && (runners <= odd._8)
          ) {

            if (placeInstructions.contains(thirdFavSelectionId)) {
              placeInstructions -= thirdFavSelectionId
            } else {

              val price = decrementPrice(decrementPrice(thirdFavOdds))

              placeInstructions += (thirdFavSelectionId ->
                PlaceInstruction(
                  selectionId = thirdFavSelectionId,
                  side = Side.BACK,
                  limitOrder = Some(LimitOrder(size = stake(price),
                    price = price,
                    persistenceType = PersistenceType.PERSIST))))
            }

            oddsBackThirdFavBetPlaced = true

            println((new time.DateTime(DateTimeZone.UTC)) + " - back third fav - " + marketId)


          }

        }

        val placeInstructionSet = placeInstructions.values.toSet


        println((new time.DateTime(DateTimeZone.UTC)) + " - fav price - " + favOdds + " - second fav price - " + secondFavOdds
          + " - third fav price - " + thirdFavOdds)

        var suspended = true
        var nonRunner = false

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
                    nonRunner = true
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


        if (placeInstructionSet.nonEmpty && !nonRunner) {

          betfairServiceNG.placeOrders(sessionToken,
            marketId = marketId,
            instructions = placeInstructionSet) onComplete {
            case Success(Some(placeExecutionReportContainer)) =>
              println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - " + placeInstructionSet.size + " non hcap bets placed")
            case _ =>
              println("error no result returned")
          }

        } else {
          println(new time.DateTime(DateTimeZone.UTC) + " - " + marketId + " - no bets placed - no non hcap bets or non runner")
        }

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring non hcap fav, second and third fav ending - " + marketId)

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

  def stake(price: Double): Double = {
    //    BigDecimal(12.00 / (price - 1.0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    2.00
  }

}