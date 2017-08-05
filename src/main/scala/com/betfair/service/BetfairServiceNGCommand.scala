package com.betfair.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import com.betfair.Configuration
import com.betfair.domain._

import scala.concurrent._
import scala.language.postfixOps

class BetfairServiceNGCommand(val config: Configuration)
                             (implicit executionContext: ExecutionContext, system: ActorSystem) {

  def makeLoginRequest(request: LoginRequest)(implicit unmarshaller: FromResponseUnmarshaller[LoginResponse]): Future[Option[LoginResponse]] = {

    import HttpCharsets._
    import HttpMethods._
    import MediaTypes._

    implicit val materializer = ActorMaterializer()

    val accept = Accept(`application/json`)
    val acceptCharset = `Accept-Charset`(`UTF-8`, HttpCharsetRange.`*`)
    val xApplication = RawHeader("X-Application", config.appKey)

    val headers = Seq(accept, acceptCharset, xApplication)

    for {
      response <- Http().singleRequest(HttpRequest(
        POST, uri = config.isoUrl + "/login?username=" + request.username + "&password=" + request.password,
        headers = headers.toList))
      entity <- unmarshaller(response)
    } yield Some(entity)

  }

  def makeKeepAliveRequest(sessionToken: String)(implicit unmarshaller: FromResponseUnmarshaller[KeepAliveResponse]): Future[Option[KeepAliveResponse]] = {

    import HttpMethods._
    import MediaTypes._

    implicit val materializer = ActorMaterializer()

    val accept = Accept(`application/json`)
    val xApplication = RawHeader("X-Application", config.appKey)
    val xAuthentication = RawHeader("X-Authentication", sessionToken)

    val headers = Seq(accept, xApplication, xAuthentication)

    for {
      response <- Http().singleRequest(HttpRequest(
        POST,
        uri = config.isoUrl + "/keepAlive",
        headers = headers.toList))
      entity <- unmarshaller(response)
    } yield Some(entity)

  }

  def makeLogoutRequest(sessionToken: String)(implicit unmarshaller: FromResponseUnmarshaller[LogoutResponse]) {

    import HttpCharsets._
    import HttpMethods._
    import MediaTypes._

    implicit val materializer = ActorMaterializer()

    val accept = Accept(`application/json`)
    val acceptCharset = `Accept-Charset`(`UTF-8`, HttpCharsetRange.`*`)
    val xApplication = RawHeader("X-Application", config.appKey)
    val xAuthentication = RawHeader("X-Authentication", sessionToken)

    val headers = Seq(accept, acceptCharset, xApplication, xAuthentication)

    Http().singleRequest(HttpRequest(
      POST,
      uri = config.isoUrl + "/logout",
      headers = headers.toList))

  }

  def makeAPIRequest[T](sessionToken: String, request: JsonrpcRequest)(implicit unmarshaller: FromResponseUnmarshaller[T]): Future[Option[T]] = {

    import HttpCharsets._
    import HttpMethods._
    import MediaTypes._
    import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

    implicit val materializer = ActorMaterializer()

    val accept = Accept(`application/json`)
    val acceptCharset = `Accept-Charset`(`UTF-8`, HttpCharsetRange.`*`)
    val xApplication = RawHeader("X-Application", config.appKey)
    val xAuthentication = RawHeader("X-Authentication", sessionToken)

    val headers = Seq(accept, acceptCharset, xApplication, xAuthentication)

    for {
      request <- Marshal(request).to[RequestEntity]
      response <- Http().singleRequest(HttpRequest(
        POST, uri = config.apiUrl, entity = request, headers = headers.toList))
      entity <- unmarshaller(response)
    } yield Some(entity)

  }

}