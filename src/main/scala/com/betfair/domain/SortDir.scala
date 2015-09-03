package com.betfair.domain

import play.api.libs.json.{Reads, Writes}

object SortDir extends Enumeration {
   type SortDir = Value
   val EARLIEST_TO_LATEST, LATEST_TO_EARLIEST = Value

   implicit val enumReads: Reads[SortDir] = EnumUtils.enumReads(SortDir)

   implicit def enumWrites: Writes[SortDir] = EnumUtils.enumWrites
 }
