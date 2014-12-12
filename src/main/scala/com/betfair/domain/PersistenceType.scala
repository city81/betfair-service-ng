package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object PersistenceType extends Enumeration {
    type PersistenceType = Value
    val LAPSE, PERSIST, MARKET_ON_CLOSE = Value

    implicit val enumReads: Reads[PersistenceType] = EnumUtils.enumReads(PersistenceType)

    implicit def enumWrites: Writes[PersistenceType] = EnumUtils.enumWrites
  }
