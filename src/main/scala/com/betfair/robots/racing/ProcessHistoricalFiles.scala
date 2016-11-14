package com.betfair.robots.racing

import java.util.Date

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer

class ProcessHistoricalFiles {

  def process = {

    var selectionIPMap = scala.collection.mutable.Map[String, ListBuffer[IPRaceData]]()

    val raceDataList = new ListBuffer[RaceData]

    val eventList = Array("5f", "6f", "7f", "1m", "1m1f", "1m2f", "1m3f", "1m4f", "1m5f", "1m6f", "1m7f", "2m",
      "2m1f", "2m2f", "2m3f", "2m4f", "2m5f", "2m6f", "2m7f", "3m",
      "3m1f", "3m2f", "3m3f", "3m4f", "3m5f", "3m6f", "3m7f")

    val scheduledOffDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")
    val latestTakenDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    val historicFiles = Array(
      "bfinf_horse_160502to160508_160511120000.csv",
      "bfinf_horse_160509to160515_160518120002.csv",
      "bfinf_horse_160523to160529_160601120000.csv",
      "bfinf_horse_160516to160522_160603082606.csv",
      "bfinf_horse_160530to160605_160608120001.csv",
      "bfinf_horse_160606to160612_160615120001.csv",
      "bfinf_horse_160613to160619_160622120002.csv",
      "bfinf_horse_160620to160626_160629120000.csv",
      "bfinf_horse_160627to160703_160706120000.csv",
      "bfinf_horse_160704to160710_160713120001.csv",
      "bfinf_horse_160711to160717_160720120003.csv",
      "bfinf_horse_160718to160724_160727120000.csv",
      "bfinf_horse_160725to160731_160803120003.csv",
      "bfinf_horse_160801to160807_160810120001.csv",
      "bfinf_horse_160808to160814_160817120001.csv",
      "bfinf_horse_160815to160821_160824120001.csv",
      "bfinf_horse_160822to160828_160831120001.csv",
      "bfinf_horse_160829to160904_160907120002.csv",
      "bfinf_horse_160905to160911_160914120003.csv",
      "bfinf_horse_160912to160918_160921120006.csv",
      "bfinf_horse_160919to160925_160928120004.csv",
      "bfinf_horse_160926to161002_161005120002.csv",
      "bfinf_horse_161003to161009_161012120002.csv",
      "bfinf_horse_161010to161016_161019120001.csv")

    for (historicFile <- historicFiles) {

      var peRaceMap = scala.collection.mutable.Map[Date, scala.collection.mutable.Map[String, SelectionData]]()
      var ipRaceMap = scala.collection.mutable.Map[Date, scala.collection.mutable.Map[String, SelectionData]]()

      val bufferedSource = io.Source.fromFile(
        //        "/Users/geraint/Google Drive/betfair/" + historicFile)
        "/home/ec2-user/betfair/" + historicFile)

      val iterator = bufferedSource.getLines

      val firstLineItems = iterator.next().replace("\"", "").split(',')

      val scheduledOffPos = firstLineItems.indexOf("SCHEDULED_OFF")
      val selectionPos = firstLineItems.indexOf("SELECTION_ID")
      val latestTakenPos = firstLineItems.indexOf("LATEST_TAKEN")
      val eventPos = firstLineItems.indexOf("EVENT")
      val inPlayPos = firstLineItems.indexOf("IN_PLAY")
      val oddsPos = firstLineItems.indexOf("ODDS")
      val winFlagPos = firstLineItems.indexOf("WIN_FLAG")
      val countryPos = firstLineItems.indexOf("COUNTRY")

      while (iterator.hasNext) {

        var lineItems = iterator.next().replace("\"", "").split(',')

        var event = lineItems(eventPos).split(" ")(0)

        if (lineItems(countryPos).equals("GB") && eventList.contains(event)) {

          //          println(lineItems(eventPos))

          var scheduledOff = scheduledOffDateFormat.parse(lineItems(scheduledOffPos))
          var selection = lineItems(selectionPos)
          var latestTaken = latestTakenDateFormat.parse(lineItems(latestTakenPos))
          var odds = lineItems(oddsPos).toDouble
          var winFlag = lineItems(winFlagPos)

          // pre event
          if (lineItems(inPlayPos).equals("PE")) {
            if (peRaceMap.contains(scheduledOff)) {
              val selectionMap = peRaceMap.get(scheduledOff).get
              if (selectionMap.contains(selection)) {
                val selectionData = selectionMap.get(selection).get
                if (selectionData.latestTaken.before(latestTaken)) {
                  selectionMap += selection -> new SelectionData(latestTaken, odds, winFlag)
                }
              } else {
                selectionMap += selection -> new SelectionData(latestTaken, odds, winFlag)
              }
            } else {
              val selectionMap = scala.collection.mutable.Map[String, SelectionData](selection -> new SelectionData(latestTaken, odds, winFlag))
              peRaceMap += scheduledOff -> selectionMap
            }
          }

          // in play
          if (lineItems(inPlayPos).equals("IP")) {
            if (ipRaceMap.contains(scheduledOff)) {
              val selectionMap = ipRaceMap.get(scheduledOff).get
              if (selectionMap.contains(selection)) {
                val selectionData = selectionMap.get(selection).get
                if (selectionData.odds > odds) {
                  selectionMap += selection -> new SelectionData(latestTaken, odds, winFlag)
                }
              } else {
                selectionMap += selection -> new SelectionData(latestTaken, odds, winFlag)
              }
            } else {
              val selectionMap = scala.collection.mutable.Map[String, SelectionData](selection -> new SelectionData(latestTaken, odds, winFlag))
              ipRaceMap += scheduledOff -> selectionMap
            }
          }


        }
      }

      for (key1 <- peRaceMap.keySet) {
        //        println
        //        println(key1)
        val peSelectionMap = peRaceMap.get(key1).get
        val ipSelectionMap = ipRaceMap.get(key1).getOrElse(new HashMap[String, SelectionData]())

        if (peSelectionMap.size == ipSelectionMap.size) {

          for (selection <- peSelectionMap.keys) {
            //            println(selection)
            val peSelectionData = peSelectionMap.get(selection).get
            //            println(peSelectionData.odds)
            val ipSelectionData = ipSelectionMap.get(selection).get
            //            println(ipSelectionData.odds)

            if (selectionIPMap.contains(selection)) {
              val ipRaceDataList = selectionIPMap.get(selection).get
              val ipRaceData = new IPRaceData(peSelectionData.latestTaken, peSelectionData.odds, ipSelectionData.odds,
                ((ipSelectionData.odds - 1.0) / (peSelectionData.odds - 1.0)))
              ipRaceDataList += ipRaceData
              selectionIPMap += selection -> ipRaceDataList
            } else {
              val ipRaceDataList = new ListBuffer[IPRaceData]
              val ipRaceData = new IPRaceData(peSelectionData.latestTaken, peSelectionData.odds, ipSelectionData.odds,
                ((ipSelectionData.odds - 1.0) / (peSelectionData.odds - 1.0)))
              ipRaceDataList += ipRaceData
              selectionIPMap += selection -> ipRaceDataList
            }

          }

        }
      }

    }

    var historicalPricesMap = Map[String, Double]()

    for (selection <- selectionIPMap.keys) {
      //      println(selection)
      val ipRaceDataList = selectionIPMap.get(selection).get.sortBy(_.scheduledOff)

      if ((ipRaceDataList.size > 3) && (ipRaceDataList.reverse.take(4).drop(1).filter(_.peOdds < 12.0).size > 0)) {

        var minOddsDrop =
          ipRaceDataList.reverse.take(4).drop(1).filter(_.peOdds < 12.0).sortBy(_.oddsDrop).reverse.toList(0).oddsDrop

        if (minOddsDrop < 0.2) {
          historicalPricesMap += selection -> minOddsDrop
        }
      }
    }

    println(historicalPricesMap.size)

    historicalPricesMap

  }

}