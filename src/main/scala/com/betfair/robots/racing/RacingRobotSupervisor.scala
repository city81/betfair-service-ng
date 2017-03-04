package com.betfair.robots.racing

import akka.actor._
import com.betfair.service.{BetfairServiceNG, BetfairServiceNGException}
import org.joda.time
import org.joda.time.DateTimeZone

import scala.concurrent.duration._

class RacingRobotSupervisor(betfairServiceNG: BetfairServiceNG) extends Actor {

  import context._

  def receive = {

    case _ =>

      val marketMonitorFavActor = context.actorOf(Props(new MarketMonitorFav(betfairServiceNG)), "market-monitor-win")

      // log in to obtain a session token and schedule the market monitoring actor to run every 3 hours
      betfairServiceNG.login.map {
        case Some(loginResponse) =>
          println((new time.DateTime(DateTimeZone.UTC)) + " logged in")
          system.scheduler.schedule(0 seconds, 3 hours, marketMonitorFavActor, MarketMonitorFav.StartMonitoring(loginResponse.token))
        case _ => throw new BetfairServiceNGException("no session token")
      }
  }
}

object RacingRobotSupervisor {
  case class Start()
}


