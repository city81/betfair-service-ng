package com.betfair

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import com.betfair.service.BetfairServiceNGCommand
import com.typesafe.config.ConfigFactory

object Boot extends App {

  val conf = ConfigFactory.load()

  val serverPort = conf.getInt("port")

  val appKey = conf.getString("betfairService.appKey")
  val username  = conf.getString("betfairService.username")
  val password = conf.getString("betfairService.password")
  val apiUrl = conf.getString("betfairService.apiUrl")
  val isoUrl = conf.getString("betfairService.isoUrl")

  val configuration = Configuration(appKey, username, password, apiUrl, isoUrl)

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")
  val log = Logging(system, getClass)

  implicit val commandExecutionContext = system.dispatcher

  implicit val command = new BetfairServiceNGCommand(configuration)

}
