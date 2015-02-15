package com.betfair.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.http.{HttpHeaders, Request, RequestListener, Response}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, Suite, SuiteMixin}

import scala.collection.mutable

trait WireMockFixture extends Suite with SuiteMixin with BeforeAndAfterEach with BeforeAndAfter {
  var wireMock: WireMock = _

  val wireMockPort: Int = 8080

  val wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().port(wireMockPort))

  after {
    wireMockServer.stop()
    wireMockServer.shutdown()
  }

  override def beforeEach() {
    wireMock = new WireMock("localhost", wireMockPort)
    wireMockServer.start()
  }

  override def afterEach() {
    wireMock.resetMappings()
    wireMockServer.stop()
  }

  def addRequestHeaderListener(): mutable.ArrayBuffer[HttpHeaders] = {
    var sentRequestHeaders = mutable.ArrayBuffer.empty[HttpHeaders]

    wireMockServer.addMockServiceRequestListener(new RequestListener() {
      override def requestReceived(request: Request, response: Response): Unit = {
        sentRequestHeaders += request.getHeaders
      }
    })
    sentRequestHeaders
  }
}
