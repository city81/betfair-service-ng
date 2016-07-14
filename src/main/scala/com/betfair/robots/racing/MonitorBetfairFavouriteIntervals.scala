package com.betfair.robots.racing

import akka.actor.Actor
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class MonitorBetfairFavouriteIntervals(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                                       marketId: String, marketStartTime: Option[DateTime])(implicit executionContext: ExecutionContext) extends Actor {

  def receive = {
    case _ => {

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring starting - " + marketId)

      var counter: Int = 5

      var favSelectionId = 0L
      var fiveMinPrice = 1000.0
      var fourMinPrice = 1000.0
      var threeMinPrice = 1000.0
      var twoMinPrice = 1000.0
      var oneMinPrice = 1000.0
      var offPrice = 1.0

      // get exchange favourite
      val exchangeFavourite = betfairServiceNG.getExchangeFavourite(sessionToken, marketId = marketId
      ) map { response =>
        response match {
          case Some(runner) =>
            favSelectionId = runner.selectionId
            fiveMinPrice = runner.ex.get.availableToBack.toSeq(0).price
            println(marketId + " - " + runner.selectionId + " - " + counter + " - " +
              runner.ex.get.availableToBack.toSeq(0).price)
          case _ =>
            println("error no result returned")
        }
      }
      Await.result(exchangeFavourite, 10 seconds)


      while (counter > 0) {

        Thread.sleep(60000)

        counter = counter - 1

        // get exchange favourite
        val priceBoundRunners = betfairServiceNG.getPriceBoundRunners(sessionToken, marketId = marketId,
          lowerPrice = 1.00, higherPrice = 20.0
        ) map { response =>
          response match {
            case Some(runners) =>
              runners foreach { runner =>
                if (runner.selectionId == favSelectionId) {
                  runner.ex match {
                    case Some(ex) =>
                      offPrice = runner.ex.get.availableToBack.toSeq(0).price
                      if (counter == 4) {
                        fourMinPrice = runner.ex.get.availableToBack.toSeq(0).price
                      }
                      if (counter == 3) {
                        threeMinPrice = runner.ex.get.availableToBack.toSeq(0).price
                      }
                      if (counter == 2) {
                        twoMinPrice = runner.ex.get.availableToBack.toSeq(0).price
                      }
                      if (counter == 1) {
                        oneMinPrice = runner.ex.get.availableToBack.toSeq(0).price
                      }
                      println(marketId + " - " + runner.selectionId + " - " + counter + " - " +
                        runner.ex.get.availableToBack.toSeq(0).price)
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

      }

//      if ((fiveMinPrice >= offPrice)
//          && (offPrice >= 1.0)
//          && (offPrice <= 5.0)) {
//
//        val placeInstructions = Set(
//
//          PlaceInstruction(
//            orderType = OrderType.LIMIT,
//            selectionId = favSelectionId,
//            side = Side.LAY,
//            limitOrder = Some(LimitOrder(size = 3.00,
//              price = 6.0,
//              persistenceType = PersistenceType.PERSIST)))
//        )
//
//        betfairServiceNG.placeOrders(sessionToken, marketId = marketId,
//          instructions = placeInstructions
//        ) onComplete {
//          case Success(Some(placeExecutionReportContainer)) =>
//            println(new time.DateTime(DateTimeZone.UTC) +
//              " - bet placed - " + marketId)
//          case _ =>
//            println(new time.DateTime(DateTimeZone.UTC) + " - error no result returned - " + marketId)
//        }
//
//      }

      if ((oneMinPrice >= offPrice)
        && (offPrice >= 1.0)
        && (offPrice <= 4.0)) {

        val placeInstructions = Set(

          PlaceInstruction(
            orderType = OrderType.LIMIT,
            selectionId = favSelectionId,
            side = Side.LAY,
            limitOrder = Some(LimitOrder(size = 2.00,
              price = 5.0,
              persistenceType = PersistenceType.PERSIST)))
        )

        betfairServiceNG.placeOrders(sessionToken, marketId = marketId,
          instructions = placeInstructions
        ) onComplete {
          case Success(Some(placeExecutionReportContainer)) =>
            println(new time.DateTime(DateTimeZone.UTC) +
              " - bet placed - " + marketId)
          case _ =>
            println(new time.DateTime(DateTimeZone.UTC) + " - error no result returned - " + marketId)
        }

      }

//      if ((oneMinPrice >= offPrice)
//        && (offPrice >= 1.0)
//        && (offPrice <= 4.0)) {
//
//        val placeInstructions = Set(
//
//          PlaceInstruction(
//            orderType = OrderType.LIMIT,
//            selectionId = favSelectionId,
//            side = Side.LAY,
//            limitOrder = Some(LimitOrder(size = 3.00,
//              price = 6.0,
//              persistenceType = PersistenceType.PERSIST)))
//        )
//
//        betfairServiceNG.placeOrders(sessionToken, marketId = marketId,
//          instructions = placeInstructions
//        ) onComplete {
//          case Success(Some(placeExecutionReportContainer)) =>
//            println(new time.DateTime(DateTimeZone.UTC) +
//              " - bet placed - " + marketId)
//          case _ =>
//            println(new time.DateTime(DateTimeZone.UTC) + " - error no result returned - " + marketId)
//        }
//
//      }

//      if ((twoMinPrice >= oneMinPrice)
//        && (oneMinPrice >= offPrice)
//        && (offPrice >= 1.0)
//        && (offPrice <= 5.0)) {
//
//        val placeInstructions = Set(
//
//          PlaceInstruction(
//            orderType = OrderType.LIMIT,
//            selectionId = favSelectionId,
//            side = Side.LAY,
//            limitOrder = Some(LimitOrder(size = 3.00,
//              price = 6.0,
//              persistenceType = PersistenceType.PERSIST)))
//        )
//
//        betfairServiceNG.placeOrders(sessionToken, marketId = marketId,
//          instructions = placeInstructions
//        ) onComplete {
//          case Success(Some(placeExecutionReportContainer)) =>
//            println(new time.DateTime(DateTimeZone.UTC) +
//              " - bet placed - " + marketId)
//          case _ =>
//            println(new time.DateTime(DateTimeZone.UTC) + " - error no result returned - " + marketId)
//        }
//
//      }

      var monitor = true

      while (monitor) {

        // get the winner
        val winner = betfairServiceNG.getWinner(sessionToken, marketId = marketId
        ) map { response =>
          response match {
            case Some(runner) =>
              if (favSelectionId.equals(runner.selectionId)) {
                println(marketId + " - " + favSelectionId + " - W")
              } else {
                println(marketId + " - " + favSelectionId + " - L")
              }
              monitor = false
            case None =>
              Thread.sleep(5000L)
            case _ =>
              monitor = false
              println(new time.DateTime(DateTimeZone.UTC) + " - error no result returned - " + marketId)
          }
        }
        Await.result(winner, 30 seconds)

      }

      println((new time.DateTime(DateTimeZone.UTC)) + " - monitoring ending - " + marketId)

    }
  }

}