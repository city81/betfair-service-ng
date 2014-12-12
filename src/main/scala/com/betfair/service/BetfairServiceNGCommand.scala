package com.betfair.service

import akka.actor.ActorSystem
import com.betfair.Configuration
import com.betfair.domain._
import spray.client.pipelining._
import spray.http.{HttpResponse, StatusCodes}
import spray.httpx.unmarshalling.FromResponseUnmarshaller

import scala.concurrent._
import scala.concurrent.duration._


final class BetfairServiceNGCommand(val config: Configuration)
                                   (implicit executionContext: ExecutionContext, system: ActorSystem) {

  var sessionToken: Option[String] = None


  private def checkStatusCodeAndUnmarshal[T](implicit unmarshaller: FromResponseUnmarshaller[T]): Future[HttpResponse] => Future[Option[T]] =
    (futRes: Future[HttpResponse]) => futRes.map {
      res =>
        if (res.status == StatusCodes.OK) Some(unmarshal[T](unmarshaller)(res)) else None
    }

  def makeLoginRequest(request: LoginRequest)(implicit unmarshaller: FromResponseUnmarshaller[LoginResponse]) {

    val pipeline =
      addHeader("Content-Type", "application/x-www-form-urlencoded") ~>
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[LoginResponse]

    val sessionTokenFuture = pipeline {
      Post("https://identitysso.betfair.com/api/login?username=" + request.username + "&password=" + request.password)
    }.map { response =>
      response match {
        case Some(response) =>
          Some(response.token)
        case _ =>
          None
      }
    }

    // wait for the future to complete as all other api calls will not work without a token
    sessionToken = Await.result(sessionTokenFuture, 10 seconds)
  }


  def makeLogoutRequest()(implicit unmarshaller: FromResponseUnmarshaller[LogoutResponse]) {

    val pipeline =
      addHeader("Content-Type", "application/x-www-form-urlencoded") ~>
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        addHeader("X-Authentication", sessionToken.get) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[LogoutResponse]

    pipeline {
      Post("https://identitysso.betfair.com/api/logout")
    }.map { response =>
      response match {
        case Some(response) =>
          Some(response.token)
        case _ =>
          None
      }
    }
  }

  def makeAPIRequest[T](request: JsonrpcRequest)(implicit unmarshaller: FromResponseUnmarshaller[T]): Future[Option[T]] = {

    import spray.httpx.PlayJsonSupport._

    println(JsonrpcRequest.writesJsonrpcRequest.writes(request))

    val pipeline =
      addHeader("Content-Type", "application/json") ~>
        addHeader("Accept", "application/json") ~>
        addHeader("Accept-Charset", "UTF-8") ~>
        addHeader("X-Application", config.appKey) ~>
        addHeader("X-Authentication", sessionToken.get) ~>
        sendReceive ~> checkStatusCodeAndUnmarshal[T]

    pipeline {
      Post("https://api.betfair.com/exchange/betting/json-rpc/v1", request)
    }

  }

}