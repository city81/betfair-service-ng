package com.betfair.domain

import com.betfair.domain.MarketProjection._

import scala.collection.mutable.Map
import reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe._

case class JsonrpcRequest(jsonrpc: String = "2.0", method: String, id: String, params: Map[String, Object])

object JsonrpcRequest {

  import play.api.libs.json.Json.JsValueWrapper
  import play.api.libs.json.{Writes, _}

  implicit val writeAnyMapFormat = new Writes[Map[String, Object]] {

    import com.betfair.domain.MarketFilter._
    import com.betfair.domain.MarketProjection._
    import com.betfair.domain.MarketSort._

    def writes(map: Map[String, Object]): JsValue = {
      Json.obj(map.map {
        case (s, a) => {
          val ret: (String, JsValueWrapper) = a match {
            case _: String => s -> JsString(a.asInstanceOf[String])
            case _: java.util.Date => s -> JsString(a.asInstanceOf[String])
            case _: Integer => s -> JsString(a.toString)
            case _: java.lang.Double => s -> JsString(a.toString)
            case _: MarketFilter => s -> Json.toJson(a.asInstanceOf[MarketFilter])
            case _: MarketSort => s -> Json.toJson(a.asInstanceOf[MarketSort])
            case _: PriceProjection => s -> Json.toJson(a.asInstanceOf[PriceProjection])
            case None => s -> JsNull
            // need to get this working with typeTag or classTag
            case market: List[MarketProjection] if market(0).isInstanceOf[MarketProjection] => s -> Json.toJson(a.asInstanceOf[List[MarketProjection]])
            case place: Set[PlaceInstruction] if place.toList(0).isInstanceOf[PlaceInstruction] => s -> Json.toJson(a.asInstanceOf[Set[PlaceInstruction]])
            case cancel: Set[CancelInstruction] if cancel.toList(0).isInstanceOf[CancelInstruction] => s -> Json.toJson(a.asInstanceOf[Set[CancelInstruction]])
            case string: Set[String] if string.toList(0).isInstanceOf[String] => s -> Json.toJson(a.asInstanceOf[Set[String]])
          }
          ret
        }
      }.toSeq: _*)
    }

  }

  implicit val writesJsonrpcRequest = Json.writes[JsonrpcRequest]
}