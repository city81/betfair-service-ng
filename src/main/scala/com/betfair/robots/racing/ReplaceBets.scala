package com.betfair.robots.racing

import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor}
import com.betfair.domain._
import com.betfair.service.BetfairServiceNG
import org.joda.time
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class ReplaceBets(betfairServiceNG: BetfairServiceNG, sessionToken: String,
                  placeExecutionReportContainer: PlaceExecutionReportContainer)(implicit executionContext: ExecutionContext) extends Actor {

  import context._

  def receive = {

    case _ => {

      placeExecutionReportContainer.result.instructionReports foreach {

        report =>

          val updateInstructions = Set(

            UpdateInstruction(
              orderType = OrderType.LIMIT,
              selectionId = runner.selectionId,
              handicap = 0.0,
              side = Side.LAY,
              limitOrder = Some(LimitOrder(size = 2.00, price = ex.availableToBack.seq.head.price,
                persistenceType = PersistenceType.PERSIST)))
          )

          betfairServiceNG.updateOrders(sessionToken,
            marketId = placeExecutionReportContainer.result.marketId,
            instructions = updateInstructions) onComplete {
            case Success(Some(updateExecutionReportContainer)) =>

              println("bet updated")

            case _ =>
              println("error no result returned")
          }

      }

    }

  }

}