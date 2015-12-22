package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain.{JsonrpcRequest, MarketFilter, EventResultContainer}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.time.{Second, Span}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable


class BetfairServiceNGCommandSpec extends UnitSpec with WireMockFixture {

  implicit val system = ActorSystem("on-spray-can")

  val sessionToken = "12345"

  val testConfig = Configuration(
    appKey = "testAppKey",
    username = "testUsername",
    password = "testPassword",
    apiUrl = "http://localhost:8080/",
    isoUrl = "http://localhost:8080/"
  )

  val service = new BetfairServiceNGCommand(testConfig)

  "The BetfairServiceNGCommand when sending http request" should {

    "add the app key to http header" in {

      import PlayJsonSupport._

      val requests = addRequestHeaderListener()

      val marketFilter = new MarketFilter()
      val params = mutable.HashMap[String, Object]("filter" -> marketFilter)
      val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listEvents", params = params)
      service.makeAPIRequest[EventResultContainer](sessionToken, request)

      val timeout = org.scalatest.concurrent.Eventually.PatienceConfig(Span(1, Second))
      eventually(requests should have length(1))(timeout)
      val headerAppToken = requests(0).getHeader("X-Application")
      headerAppToken.firstValue() should be("testAppKey")
    }

    "add the session token to http header" in {

      import PlayJsonSupport._

      val requests = addRequestHeaderListener()

      val marketFilter = new MarketFilter()
      val params = mutable.HashMap[String, Object]("filter" -> marketFilter)
      val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listEvents", params = params)
      service.makeAPIRequest[EventResultContainer](sessionToken, request)

      val timeout = org.scalatest.concurrent.Eventually.PatienceConfig(Span(1, Second))
      eventually(requests should have length(1))(timeout)
      val headerSessionToken = requests(0).getHeader("X-Authentication")
      headerSessionToken.firstValue() should be(sessionToken)
    }

  }

}