package com.betfair.domain

import scala.collection.mutable.Map


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
            case JsArray(elements) => s -> JsArray(elements)
            case _: List[MarketProjection] => s -> Json.toJson(a.asInstanceOf[List[MarketProjection]])
            case _: Set[PlaceInstruction] if s == "instructions" => s -> Json.toJson(a.asInstanceOf[Set[PlaceInstruction]])
            case _: Set[String] => s -> Json.toJson(a.asInstanceOf[Set[String]])
            case _ => s -> JsArray(a.asInstanceOf[List[Int]].map(JsNumber(_)))
          }
          ret
        }
      }.toSeq: _*)
    }
  }

  implicit val writesJsonrpcRequest = Json.writes[JsonrpcRequest]
}

