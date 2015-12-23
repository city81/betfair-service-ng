package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain._
import spray.client.pipelining._
import spray.http.{HttpResponse, StatusCodes}
import spray.httpx.encoding.{Deflate, Gzip}
import spray.httpx.unmarshalling.FromResponseUnmarshaller

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

class BetfairServiceNGCommand(val config: Configuration)
                             (implicit executionContext: ExecutionContext, system: ActorSystem) {

  private def checkStatusCodeAndUnmarshal[T](implicit unmarshaller: FromResponseUnmarshaller[T]): Future[HttpResponse] => Future[Option[T]] =
    (futRes: Future[HttpResponse]) => futRes.map {
      res =>
        if (res.status == StatusCodes.OK) {
//          println(res) // TODO replace with logging
          Some(unmarshal[T](unmarshaller)(res))
        } else None
    }

  def makeLoginRequest(request: LoginRequest)(implicit unmarshaller: FromResponseUnmarshaller[LoginResponse]): Future[Option[LoginResponse]] = {

    val pipeline =
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[LoginResponse]

    pipeline {
      Post(config.isoUrl + "/login?username=" + request.username + "&password=" + request.password)
    }

  }

  def makeLogoutRequest(sessionToken: String)(implicit unmarshaller: FromResponseUnmarshaller[LogoutResponse]) {

    val pipeline =
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        addHeader("X-Authentication", sessionToken) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[LogoutResponse]

    pipeline {
      Post(config.isoUrl + "/logout")
    }

  }

  def makeKeepAliveRequest(sessionToken: String)(implicit unmarshaller: FromResponseUnmarshaller[KeepAliveResponse]): Future[Option[KeepAliveResponse]] = {

    val pipeline =
      addHeader("Accept", "application/json") ~>
        addHeader("X-Application", config.appKey) ~>
        addHeader("X-Authentication", sessionToken) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[KeepAliveResponse]

    pipeline {
      Post(config.isoUrl + "/keepAlive")
    }

  }

  def makeAPIRequest[T](sessionToken: String, request: JsonrpcRequest)(implicit unmarshaller: FromResponseUnmarshaller[T]): Future[Option[T]] = {

    import spray.httpx.PlayJsonSupport._

    val pipeline =
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        addHeader("X-Authentication", sessionToken) ~>
        sendReceive ~>
        decode(Gzip) ~> decode(Deflate) ~>
        checkStatusCodeAndUnmarshal[T]

    pipeline {
      Post(config.apiUrl, request)
    }

  }

}