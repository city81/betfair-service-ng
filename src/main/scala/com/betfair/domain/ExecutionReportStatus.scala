package com.betfair.domain

import play.api.libs.json.{Writes, Reads}

object ExecutionReportStatus extends Enumeration {
    type ExecutionReportStatus = Value
    val SUCCESS, FAILURE, PROCESSED_WITH_ERRORS, TIMEOUT = Value

    implicit val enumReads: Reads[ExecutionReportStatus] = EnumUtils.enumReads(ExecutionReportStatus)

    implicit def enumWrites: Writes[ExecutionReportStatus] = EnumUtils.enumWrites
  }