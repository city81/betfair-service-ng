package com.betfair.service

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller
import com.betfair.Configuration
import com.betfair.domain._
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent._
import org.scalatest.{FlatSpec, MustMatchers}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BetfairServiceNGSpec extends FlatSpec with MustMatchers with MockFactory with ScalaFutures {

  implicit val system = ActorSystem("on-spray-can")

  // TODO make config implicit to avoid this workaround
  val testConfig = Configuration(
    appKey = "testAppKey",
    username = "testUsername",
    password = "testPassword",
    apiUrl = "localhost",
    isoUrl = "localhost"
  )
  class MockCommand extends BetfairServiceNGCommand(testConfig)

  val testCommand = mock[MockCommand]


  "The BetfairServiceNGCommand" should "retrieve list of events based on supplied market filter" in {

    val sessionToken = "12345"
    val marketFilter = new MarketFilter()
    val params = mutable.HashMap[String, Object]("filter" -> marketFilter)
    val request = new JsonrpcRequest(id = "1", method = "SportsAPING/v1.0/listEvents", params = params)
    val eventTypeId = "123"
    val event = Event(eventTypeId, "event", None, "GMT", None, new DateTime())
    val eventResult = EventResult(event, 1)
    val listEventsResponseSuccess = EventResultContainer(List(eventResult))

    (testCommand.makeAPIRequest(_: String, _: JsonrpcRequest)(_: FromResponseUnmarshaller[EventResultContainer])).expects(sessionToken, request, *).returning(Future.successful(Some(listEventsResponseSuccess)))

    val testService = new BetfairServiceNG(testConfig, testCommand)

    val result = testService.listEvents(sessionToken, marketFilter)

    whenReady(result) { res =>
      res match {
        case Some(container) =>
          container.result.size must be(1)
          container.result(0).marketCount must be(1)
          container.result(0).event.id must be(eventTypeId)
        case _ =>
          fail()
      }
    }
  }

}
