package com.betfair.robots.football

import java.util.zip.ZipFile

import scala.io.BufferedSource

object FootballProcessor extends App {

  import collection.JavaConverters._

  val sportsIdPos = 0
  val actualOffPos = 6
  val selectionNamePos = 8
  val latestTakenPos = 12
  val eventPos = 5
  val oddsPos = 9
  val winFlagPos = 14
  val descriptionPos = 3

  val scheduledOffDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")
  val latestTakenDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

  val historicFiles = Array(
    "bfinf_other_170220to170226_170301123840.zip"
  )

  val preEventMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, PreEventSelectionData]]()
  val inPlayMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, InPlaySelectionData]]()

  for (historicFile <- historicFiles) {

    val zf = new ZipFile("/Users/geraint/Downloads/" + historicFile)
    val entries = zf.entries().asScala
    val iterator = new BufferedSource(zf.getInputStream(entries.next())).getLines.drop(1)

    iterator.foreach(l => {

      val lineItems = l.replace("\"", "").split(',')

      val event = lineItems(eventPos)

      if (lineItems(sportsIdPos).contains("1")
        && event.equals("Match Odds")
        && lineItems(descriptionPos).contains("English Premier League")
        && !lineItems(actualOffPos).isEmpty) {

        val actualOff = scheduledOffDateFormat.parse(lineItems(actualOffPos))
        val latestTaken = latestTakenDateFormat.parse(lineItems(latestTakenPos))
        val odds = lineItems(oddsPos).toDouble
        val winFlag = lineItems(winFlagPos)
        val description = lineItems(descriptionPos)
        val selection = lineItems(selectionNamePos)

        if (actualOff.after(latestTaken)) {

          // pre event

          val selectionMap = preEventMap.getOrElse(description, scala.collection.mutable.Map[String, PreEventSelectionData]())

          val selectionData = selectionMap.getOrElse(selection, PreEventSelectionData(latestTaken, odds))

          if (!selectionData.latestTaken.after(latestTaken)) {
            selectionMap += selection -> PreEventSelectionData(latestTaken, odds)
          }

          preEventMap += description -> selectionMap

        } else {

          // in play

          val selectionMap = inPlayMap.getOrElse(description, scala.collection.mutable.Map[String, InPlaySelectionData]())

          val selectionData = selectionMap.getOrElse(selection, InPlaySelectionData(odds, winFlag))

          if (odds >= selectionData.odds) {
            selectionMap += selection -> InPlaySelectionData(odds, winFlag)
          }

          inPlayMap += description -> selectionMap

        }
      }
    })

  }

  for ((preEventKey, preEventMap) <- preEventMap) {
    println(preEventKey)
    val inPlaySelectionMap = inPlayMap(preEventKey)
    for ((selection, selectionData) <- preEventMap) {
      println(selection)
      println("odds at the off " + selectionData.odds)
      println("longest odds matched in running " + inPlaySelectionMap(selection).odds)
    }
    println
  }


}