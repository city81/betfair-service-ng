package com.betfair.robots.racing

import akka.actor.{ActorSystem, Props}
import com.betfair.Configuration
import RacingRobotSupervisor.Start
import com.betfair.robots.racing.RacingRobotSupervisor
import com.betfair.service.{BetfairServiceNG, BetfairServiceNGCommand}
import com.typesafe.config.ConfigFactory

object RacingRobot extends App {

  // create the systema nd the dispatcher
  implicit val system = ActorSystem("racing-robot")
  implicit val executionContext = system.dispatcher

  // load and crrate the config object
  val conf = ConfigFactory.load()
  val appKey = conf.getString("betfairService.appKey")
  val username = conf.getString("betfairService.username")
  val password = conf.getString("betfairService.password")
  val apiUrl = conf.getString("betfairService.apiUrl")
  val isoUrl = conf.getString("betfairService.isoUrl")
  val config = new Configuration(appKey, username, password, apiUrl, isoUrl)

  // create command and api objects
  val betfairServiceNGCommand = new BetfairServiceNGCommand(config)
  val betfairServiceNG = new BetfairServiceNG(config, betfairServiceNGCommand)

  // create top level actor
  val racingRobotSupervisor = system.actorOf(Props(new RacingRobotSupervisor(betfairServiceNG)), "racing-robot-supervisor")

  racingRobotSupervisor ! Start

}