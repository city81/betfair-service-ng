package com.betfair.robots.forex

import scala.collection.mutable.ListBuffer

object ProcessForexCandle extends App {

  def process() = {

    val markets = Vector("EURGBP")

    for (market <- markets) {

      println
      println("Market: " + market)
      println("---")
      println

      //
      // load data into a list
      //
      val forexDataList = new ListBuffer[ForexDetailCandle]

      val fileLines = io.Source.fromFile("/Users/geraint/Documents/FOREX/" + market + "_Candle.csv").getLines.drop(1).toList

      for (fileLine <- fileLines) {

        var fileLineItems = fileLine.split(",")

        if ((fileLineItems(3) != null) && (!fileLineItems(3).isEmpty)) {

          var forexDetail = new ForexDetailCandle(
            fileLineItems(0),
            fileLineItems(1),
            fileLineItems(2),
            fileLineItems(3).toDouble,
            fileLineItems(4).toDouble,
            fileLineItems(5).toDouble,
            fileLineItems(6).toDouble
          )

          forexDataList += forexDetail

        }

      }

//      for (dropNumber <- 0 until (forexDataList.length - 5)) {
        for (dropNumber <- 0 until 1) {

        var hourlyForexDataList = forexDataList.reverse.toList.drop(dropNumber)

        // 1 hr analysis
        candlestick(hourlyForexDataList, "1 hr")

        // 2 hr analysis
        {
          var twoHourlyForexDataList: List[ForexDetailCandle] = List.empty
          val lastHour = hourlyForexDataList(0).time.split(":")
          val firstHourList = Array("00", "02", "04", "06", "08", "10", "12", "14", "16", "18", "20", "22")
          if (firstHourList contains lastHour(0)) {
            twoHourlyForexDataList = populateTwoHourlyList(hourlyForexDataList.drop(1))
          } else {
            twoHourlyForexDataList = populateTwoHourlyList(hourlyForexDataList)
          }
          candlestick(twoHourlyForexDataList, "2 hr")
        }

        // 3 hr analysis
        {
          var threeHourlyForexDataList: List[ForexDetailCandle] = List.empty
          val lastHour = hourlyForexDataList(0).time.split(":")
          val firstHourList = Array("00", "03", "06", "09", "12", "15", "18", "21")
          val secondHourList = Array("01", "04", "07", "10", "13", "16", "19", "22")
          if (firstHourList contains lastHour(0)) {
            threeHourlyForexDataList = populateThreeHourlyList(hourlyForexDataList.drop(1))
          } else if (secondHourList contains lastHour(0)) {
            threeHourlyForexDataList = populateThreeHourlyList(hourlyForexDataList.drop(2))
          } else {
            threeHourlyForexDataList = populateThreeHourlyList(hourlyForexDataList)
          }
          candlestick(threeHourlyForexDataList, "3 hr")
        }

        // 4 hr analysis
        {
          var fourHourlyForexDataList: List[ForexDetailCandle] = List.empty
          val lastHour = hourlyForexDataList(0).time.split(":")
          val firstHourList = Array("00", "04", "08", "12", "16", "20")
          val secondHourList = Array("01", "05", "09", "13", "17", "21")
          val thirdHourList = Array("02", "06", "10", "14", "18", "22")
          if (firstHourList contains lastHour(0)) {
            fourHourlyForexDataList = populateFourHourlyList(hourlyForexDataList.drop(1))
          } else if (secondHourList contains lastHour(0)) {
            fourHourlyForexDataList = populateFourHourlyList(hourlyForexDataList.drop(2))
          } else if (thirdHourList contains lastHour(0)) {
            fourHourlyForexDataList = populateFourHourlyList(hourlyForexDataList.drop(3))
          } else {
            fourHourlyForexDataList = populateFourHourlyList(hourlyForexDataList)
          }
          candlestick(fourHourlyForexDataList, "4 hr")
        }

        // 1 day analysis
        {
          var oneDayForexDataList: List[ForexDetailCandle] = List.empty

          var dropCount = 0
          while (!hourlyForexDataList(dropCount).time.split(":")(0).equals("23")) {
            dropCount = dropCount + 1
          }

//          candlestick(populateOneDayList(hourlyForexDataList.drop(dropCount)), "1 day")

        }

        println("---")
        println

      }
    }
  }

  def populateTwoHourlyList(forexDataList: List[ForexDetailCandle]): List[ForexDetailCandle] = {

    val twoHourlyForexDataList = new ListBuffer[ForexDetailCandle]

    for (i <- 0 until forexDataList.length by 2) {

      var forexDetail = forexDataList(i)

      var twoHourlyForexDetail = new ForexDetailCandle(
        forexDetail.market,
        forexDetail.date,
        forexDetail.time,
        forexDetail.open,
        forexDetail.high,
        forexDetail.low,
        forexDetail.close
      )

      // update with second hour
      twoHourlyForexDetail.open = forexDataList(i + 1).open
      twoHourlyForexDetail.date = forexDataList(i + 1).date
      twoHourlyForexDetail.time = forexDataList(i + 1).time

      if (forexDataList(i + 1).high > twoHourlyForexDetail.high) {
        twoHourlyForexDetail.high = forexDataList(i + 1).high
      }
      if (forexDataList(i + 1).low < twoHourlyForexDetail.low) {
        twoHourlyForexDetail.low = forexDataList(i + 1).low
      }

      twoHourlyForexDataList += twoHourlyForexDetail

    }

    twoHourlyForexDataList.toList
  }

  def populateThreeHourlyList(forexDataList: List[ForexDetailCandle]): List[ForexDetailCandle] = {

    val threeHourlyForexDataList = new ListBuffer[ForexDetailCandle]

    for (i <- 0 until forexDataList.length by 3) {

      var forexDetail = forexDataList(i)

      var threeHourlyForexDetail = new ForexDetailCandle(
        forexDetail.market,
        forexDetail.date,
        forexDetail.time,
        forexDetail.open,
        forexDetail.high,
        forexDetail.low,
        forexDetail.close
      )

      // update with second hour
      if (forexDataList(i + 1).high > threeHourlyForexDetail.high) {
        threeHourlyForexDetail.high = forexDataList(i + 1).high
      }
      if (forexDataList(i + 1).low < threeHourlyForexDetail.low) {
        threeHourlyForexDetail.low = forexDataList(i + 1).low
      }

      // update with third hour
      threeHourlyForexDetail.open = forexDataList(i + 2).open
      threeHourlyForexDetail.date = forexDataList(i + 2).date
      threeHourlyForexDetail.time = forexDataList(i + 2).time

      if (forexDataList(i + 2).high > threeHourlyForexDetail.high) {
        threeHourlyForexDetail.high = forexDataList(i + 2).high
      }
      if (forexDataList(i + 2).low < threeHourlyForexDetail.low) {
        threeHourlyForexDetail.low = forexDataList(i + 2).low
      }

      threeHourlyForexDataList += threeHourlyForexDetail

    }

    threeHourlyForexDataList.toList
  }

  def populateFourHourlyList(forexDataList: List[ForexDetailCandle]): List[ForexDetailCandle] = {

    val fourHourlyForexDataList = new ListBuffer[ForexDetailCandle]

    for (i <- 0 until forexDataList.length by 4) {

      var forexDetail = forexDataList(i)

      var fourHourlyForexDetail = new ForexDetailCandle(
        forexDetail.market,
        forexDetail.date,
        forexDetail.time,
        forexDetail.open,
        forexDetail.high,
        forexDetail.low,
        forexDetail.close
      )

      // update with second hour
      if (forexDataList(i + 1).high > fourHourlyForexDetail.high) {
        fourHourlyForexDetail.high = forexDataList(i + 1).high
      }
      if (forexDataList(i + 1).low < fourHourlyForexDetail.low) {
        fourHourlyForexDetail.low = forexDataList(i + 1).low
      }

      // update with third hour
      if (forexDataList(i + 2).high > fourHourlyForexDetail.high) {
        fourHourlyForexDetail.high = forexDataList(i + 2).high
      }
      if (forexDataList(i + 2).low < fourHourlyForexDetail.low) {
        fourHourlyForexDetail.low = forexDataList(i + 2).low
      }

      // update with fourth hour
      fourHourlyForexDetail.open = forexDataList(i + 3).open
      fourHourlyForexDetail.date = forexDataList(i + 3).date
      fourHourlyForexDetail.time = forexDataList(i + 3).time

      if (forexDataList(i + 3).high > fourHourlyForexDetail.high) {
        fourHourlyForexDetail.high = forexDataList(i + 3).high
      }
      if (forexDataList(i + 3).low < fourHourlyForexDetail.low) {
        fourHourlyForexDetail.low = forexDataList(i + 3).low
      }

      fourHourlyForexDataList += fourHourlyForexDetail

    }

    fourHourlyForexDataList.toList
  }

  def populateOneDayList(forexDataList: List[ForexDetailCandle]): List[ForexDetailCandle] = {

    val oneDayForexDataList = new ListBuffer[ForexDetailCandle]

    for (i <- 0 until forexDataList.length by 24) {

      var forexDetail = forexDataList(i)

      var oneDayForexDetail = new ForexDetailCandle(
        forexDetail.market,
        forexDetail.date,
        forexDetail.time,
        forexDetail.open,
        forexDetail.high,
        forexDetail.low,
        forexDetail.close
      )

      for (j <- 1 until 23) {

        if (forexDataList(i + j).high > oneDayForexDetail.high) {
          oneDayForexDetail.high = forexDataList(i + j).high
        }
        if (forexDataList(i + j).low < oneDayForexDetail.low) {
          oneDayForexDetail.low = forexDataList(i + j).low
        }

      }

      // update with opening hour
      oneDayForexDetail.open = forexDataList(i + 23).open
      oneDayForexDetail.date = forexDataList(i + 23).date
      oneDayForexDetail.time = forexDataList(i + 23).time

      if (forexDataList(i + 11).high > oneDayForexDetail.high) {
        oneDayForexDetail.high = forexDataList(i + 23).high
      }
      if (forexDataList(i + 11).low < oneDayForexDetail.low) {
        oneDayForexDetail.low = forexDataList(i + 23).low
      }

      oneDayForexDataList += oneDayForexDetail

    }

    oneDayForexDataList.toList
  }

  def candlestick(forexDataList: List[ForexDetailCandle], timeframe: String): Unit = {

    println(timeframe)

    var forexData = forexDataList(0)
    var forexDataMinusOne = forexDataList(1)
    var forexDataMinusTwo = forexDataList(2)

    println(forexData.date + " " + forexData.time)

    if (forexData.open == forexData.close) {
      println("Doji")
    }
    if ((forexData.close < forexData.open) &&
      (forexData.close == forexData.low) && (forexData.open == forexData.high)) {
      println("Bearish Black Marubozu")
    }
    if ((forexData.close > forexData.open)
      && (forexData.close == forexData.high) && (forexData.open == forexData.low)) {
      println("Bullish White Marubozu")
    }
    if ((forexDataMinusOne.open > forexDataMinusOne.close)
      && (forexData.close > forexData.open) && (forexData.close >= forexDataMinusOne.open)
      && (forexDataMinusOne.close >= forexData.open)
      && ((forexData.close - forexData.open) > (forexDataMinusOne.open - forexDataMinusOne.close))) {
      println("Bullish Engulfing")
    }
    if ((forexDataMinusOne.close > forexDataMinusOne.open)
      && (forexData.open > forexData.close) &&
      (forexData.open >= forexDataMinusOne.close) &&
      (forexDataMinusOne.open >= forexData.close)
      && ((forexData.open - forexData.close) > (forexDataMinusOne.close - forexDataMinusOne.open))) {
      println("Bearish Engulfing");
    }
    if (((forexData.close == forexData.high)
      && (forexData.close > forexData.open)
      && ((forexData.close - forexData.open) * 3.0) < (forexData.high - forexData.low)) ||
      ((forexData.open == forexData.high)
        && (forexData.open > forexData.close)
        && ((forexData.open - forexData.close) * 3.0) < (forexData.high - forexData.low))) {
      println("Hammer / Hanging Man")
    }
    if (((forexData.close == forexData.low)
      && (forexData.close < forexData.open)
      && ((forexData.open - forexData.close) * 3.0) < (forexData.high - forexData.low)) ||
      ((forexData.open == forexData.low)
        && (forexData.open < forexData.close)
        && ((forexData.close - forexData.open) * 3.0) < (forexData.high - forexData.low))) {
      println("Inverted Hammer / Shooting Star")
    }
    if ((forexDataMinusTwo.open > forexDataMinusTwo.close) &&
      ((forexDataMinusTwo.open - forexDataMinusTwo.close) / (.001 + forexDataMinusTwo.high - forexDataMinusTwo.low) > .6) &&
      (forexDataMinusTwo.close > forexDataMinusOne.open) && (forexDataMinusOne.open > forexDataMinusOne.close) &&
      ((forexDataMinusOne.high - forexDataMinusOne.low) > (3 * (forexDataMinusOne.close - forexDataMinusOne.open))) &&
      (forexData.close > forexData.open) && (forexData.open > forexDataMinusOne.open)) {
      println("Bullish Morning Star");
    }
    if ((forexDataMinusTwo.close > forexDataMinusTwo.open) &&
      ((forexDataMinusTwo.close - forexDataMinusTwo.open) / (.001 + forexDataMinusTwo.high - forexDataMinusTwo.low) > .6) &&
      (forexDataMinusTwo.close < forexDataMinusOne.open) && (forexDataMinusOne.close > forexDataMinusOne.open)
      && ((forexDataMinusOne.high - forexDataMinusOne.low) > (3 * (forexDataMinusOne.close - forexDataMinusOne.open))) &&
      (forexData.open > forexData.close) && (forexData.open < forexDataMinusOne.open)) {
      println("Bearish Evening Star");
    }
    if ((forexData.close > forexData.open * 1.01) && (forexDataMinusOne.close > forexDataMinusOne.open * 1.01)
      && (forexDataMinusTwo.close > forexDataMinusTwo.open * 1.01)
      && (forexData.close > forexDataMinusOne.close) &&
      (forexDataMinusOne.close > forexDataMinusTwo.close)
      && (forexData.open > forexDataMinusOne.open)
      && (forexDataMinusOne.open > forexDataMinusTwo.open) &&
      (((forexData.high - forexData.close) / (forexData.high - forexData.low)) < .2)
      && (((forexDataMinusOne.high - forexDataMinusOne.close) / (forexDataMinusOne.high - forexDataMinusOne.low)) < .2)
      && (((forexDataMinusTwo.high - forexDataMinusTwo.close) / (forexDataMinusTwo.high - forexDataMinusTwo.low)) < .2)) {
      println("Bullish Three White Soldiers");
    }
    if ((forexData.open > forexData.close * 1.01)
      && (forexDataMinusOne.open > forexDataMinusOne.close * 1.01)
      && (forexDataMinusTwo.open > forexDataMinusTwo.close * 1.01)
      && (forexData.close < forexDataMinusOne.close)
      && (forexDataMinusOne.close < forexDataMinusTwo.close)
      && (forexData.open > forexDataMinusOne.close)
      && (forexData.open < forexDataMinusOne.open) &&
      (forexDataMinusOne.open > forexDataMinusTwo.close) && (forexDataMinusOne.open < forexDataMinusTwo.open)
      && (((forexData.close - forexData.low) / (forexData.high - forexData.low)) < .2)
      && (((forexDataMinusOne.close - forexDataMinusOne.low) / (forexDataMinusOne.high - forexDataMinusOne.low)) < .2)
      && (((forexDataMinusTwo.close - forexDataMinusTwo.low) / (forexDataMinusTwo.high - forexDataMinusTwo.low)) < .2)) {
      println("Bearish Three Black Crows");
    }
    if ((forexDataMinusOne.open > forexDataMinusOne.close) &&
      (forexData.close > forexData.open) &&
      (forexData.open > forexDataMinusOne.open) &&
      (forexData.low > forexDataMinusOne.high)) {
      println("Bullish Kicker")
    }
    if ((forexDataMinusOne.open < forexDataMinusOne.close) &&
      (forexData.open > forexData.close) &&
      (forexData.open < forexDataMinusOne.open) &&
      (forexDataMinusOne.low > forexData.high)) {
      println("Bearish Kicker")
    }
    if ((forexDataMinusOne.open > forexDataMinusOne.close) &&
      (forexData.close > forexData.open) &&
      (forexDataMinusOne.close > forexData.open) &&
      (forexDataMinusOne.open > forexData.close) &&
      (forexData.close >
        ((forexDataMinusOne.open - forexDataMinusOne.close) / 2.0)
          + forexDataMinusOne.close)) {
      println("Bullish Piercing")
    }
    if ((forexDataMinusOne.open < forexDataMinusOne.close) &&
      (forexData.close < forexData.open) &&
      (forexDataMinusOne.close < forexData.open) &&
      (forexDataMinusOne.open < forexData.close) &&
      (forexData.close <
        ((forexDataMinusOne.close - forexDataMinusOne.open) / 2.0)
          + forexDataMinusOne.open)) {
      println("Bearish Dark Cloud Cover")
    }
    if ((forexDataMinusTwo.open > forexDataMinusTwo.close) &&
      (forexDataMinusOne.close > forexDataMinusOne.open) &&
      (forexDataMinusTwo.close > forexDataMinusOne.open) &&
      (forexDataMinusTwo.open < forexDataMinusOne.close) &&
      (forexData.close > forexDataMinusOne.close) &&
      (forexData.close > forexData.open)) {
      println("Bullish Engulfing Three Outside Up")
    }
    if ((forexDataMinusTwo.open > forexDataMinusTwo.close)
      && (forexDataMinusOne.close > forexDataMinusOne.open)
      && (forexDataMinusOne.close < forexDataMinusTwo.open)
      && (forexDataMinusTwo.close < forexDataMinusOne.open)
      && (forexData.close > forexData.open)
      && (forexData.close > forexDataMinusOne.close)
      && (forexData.open > forexDataMinusOne.open)) {
      println("Bullish Engulfing Three Inside Up")
    }
    if ((forexDataMinusTwo.open < forexDataMinusTwo.close) &&
      (forexDataMinusOne.close < forexDataMinusOne.open) &&
      (forexDataMinusTwo.close < forexDataMinusOne.open) &&
      (forexDataMinusTwo.open > forexDataMinusOne.close) &&
      (forexData.close < forexDataMinusOne.close) &&
      (forexData.close < forexData.open)) {
      println("Bearish Engulfing Three Outside Down")
    }
    if ((forexDataMinusTwo.close > forexDataMinusTwo.open) && (forexDataMinusOne.open > forexDataMinusOne.close)
      && (forexDataMinusOne.open <= forexDataMinusTwo.close) && (forexDataMinusTwo.open <= forexDataMinusOne.close) &&
      ((forexDataMinusOne.open - forexDataMinusOne.close) < (forexDataMinusTwo.close - forexDataMinusTwo.open))
      && (forexData.open > forexData.close) && (forexData.close > forexDataMinusOne.close)) {
      println("Bearish Engulfing Three Inside Down")
    }
    if ((forexDataMinusOne.open > forexDataMinusOne.close) &&
      (forexData.close > forexData.open) &&
      (forexDataMinusOne.close < forexData.open) &&
      (forexDataMinusOne.open > forexData.close)) {
      println("Bullish Harami")
    }
    if ((forexDataMinusOne.open < forexDataMinusOne.close) &&
      (forexData.close < forexData.open) &&
      (forexDataMinusOne.close > forexData.open) &&
      (forexDataMinusOne.open < forexData.close)) {
      println("Bearish Harami")
    }
    println
  }

  process

}