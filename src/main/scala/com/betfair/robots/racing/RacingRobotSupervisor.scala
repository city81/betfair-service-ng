package com.betfair.robots.racing

import akka.actor._
import com.betfair.robots.racing.MarketMonitor.StartMonitoring
import com.betfair.service.{BetfairServiceNG, BetfairServiceNGException}

import scala.concurrent.duration._

class RacingRobotSupervisor(betfairServiceNG: BetfairServiceNG) extends Actor {

  import context._

  def receive = {

    case _ =>

      val marketMonitorActor = context.actorOf(Props(new MarketMonitor(betfairServiceNG)), "market-monitor")

      // log in to obtain a session token and schedule the market monitoring actor to run every 3 hours
      betfairServiceNG.login.map {
        case Some(loginResponse) =>
          system.scheduler.schedule(0 seconds, 3 hours, marketMonitorActor, StartMonitoring(loginResponse.token))
        case _ => throw new BetfairServiceNGException("no session token")
      }
  }
}

object RacingRobotSupervisor {
  case class Start()
}


