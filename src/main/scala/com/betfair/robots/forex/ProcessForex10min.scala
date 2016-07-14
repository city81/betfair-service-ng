package com.betfair.robots.forex

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ProcessForex10min extends App {

  def process() = {

    //
    // load data into a list
    //

    var profitMap: mutable.Map[String, (Double, String)] = mutable.Map.empty

    var winnersPlusLosersThreshold: Int = 0
    var profitThreshold: Double = 40.0

    val forexDataList = new ListBuffer[ForexDetail]

    val fileLines = io.Source.fromFile("/Users/geraint/Documents/FOREX/Forex10min.csv").getLines.drop(1).toList

    for (fileLine <- fileLines) {

      var fileLineItems = fileLine.split(",")

      //      println(fileLine)

      if ((fileLineItems(3) != null) && (!fileLineItems(3).isEmpty)) {

        var forexDetail = new ForexDetail(
          fileLineItems(0),
          fileLineItems(1),
          fileLineItems(2),
          fileLineItems(3).toDouble,
          fileLineItems(4),
          fileLineItems(5).toDouble,
          fileLineItems(6).toDouble,
          fileLineItems(7).toDouble,
          fileLineItems(8).toDouble,
          fileLineItems(9).toDouble,
          fileLineItems(10).toDouble,
          fileLineItems(11).toDouble,
          fileLineItems(12).toDouble,
          fileLineItems(13).toDouble,
          fileLineItems(14).toDouble,
          fileLineItems(15).toDouble,
          fileLineItems(16).toDouble,
          fileLineItems(17).toDouble,
          fileLineItems(18).toDouble,
          fileLineItems(19).toDouble,
          fileLineItems(20).toDouble,
          fileLineItems(21).toDouble,
          fileLineItems(22).toDouble,
          fileLineItems(23).toDouble,
          fileLineItems(24).toDouble,
          fileLineItems(25).toDouble,
          fileLineItems(26).toDouble
        )

        forexDataList += forexDetail

      }

    }

    val markets = Vector("EURGBP")

    println
    println("trending rsi and macd - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour > forexData.rsiMinusTwoHours) &&
              (forexData.macd > forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour > forexData.macdMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour < forexData.rsiMinusTwoHours) &&
              (forexData.macd < forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour < forexData.macdMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi and macd - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi and macd - go with")
            }

          }

          //                            }

        }
      }
    }

    // trending rsi - going higher?
    println
    println("trending rsi - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour > forexData.rsiMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour < forexData.rsiMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi - go with")
            }

          }
          //                  }
        }

      }
    }

    // rsi over under - going higher?
    println
    println("rsi over 60 under 40 - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > 60.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < 40.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " rsi over 60 under 40 - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " rsi over 60 under 40 - go with")
            }

          }
          //                  }
        }

      }
    }

    // trending macd - going higher?
    println
    println("trending (3) macd - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour > forexData.macdMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour < forexData.macdMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending (3) macd - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending (3) macd - go with")
            }

          }
          //                  }
        }
      }
    }

    // trending macd - going higher?
    println
    println("trending (2) macd - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > forexData.macdMinusOneHour)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < forexData.macdMinusOneHour)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending (2) macd - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending (2) macd - go with")
            }

          }
          //                  }
        }
      }
    }

    println
    println("trending - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market)
//              &&
//              (times.contains(forexData.time))
            ) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market)
//              &&
//              (times.contains(forexData.time))
            ) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending - go with")
            }

          }

          //                  }

        }
      }
    }


    // under over macd - going higher?
    println
    println("above high below low macd - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > 0.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < 0.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above high below low macd - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above high below low macd - go with")
            }

          }
          //                  }
        }
      }
    }

    // under over macd - going higher?
    println
    println("above high below low macd two hours - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macdMinusTwoHours > 0.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macdMinusTwoHours < 0.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above high below low macd two hours - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above high below low macd two hours - go with")
            }

          }
          //                  }
        }
      }
    }

    // under over macd - going higher?
    println
    println("below high above low macd - go with")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < 0.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > 0.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " below high above low macd - go with")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " below high above low macd - go with")
            }

          }
          //                  }
        }
      }
    }

    println
    println("trending rsi and macd - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour > forexData.rsiMinusTwoHours) &&
              (forexData.macd > forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour > forexData.macdMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour < forexData.rsiMinusTwoHours) &&
              (forexData.macd < forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour < forexData.macdMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi and macd - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi and macd - go against")
            }

          }

          //                  }
        }
      }
    }

    println
    println("trending rsi - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour > forexData.rsiMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              }  else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < forexData.rsiMinusOneHour) &&
              (forexData.rsiMinusOneHour < forexData.rsiMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending rsi - go against")
            }

          }
          //                  }
        }
      }
    }

    println
    println("rsi over 60 under 40 - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi > 60.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              }  else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.rsi < 40.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " rsi over 60 under 40 - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " rsi over 60 under 40 - go against")
            }

          }
          //                  }
        }
      }
    }

    println
    println("trending macd - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour > forexData.macdMinusTwoHours)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < forexData.macdMinusOneHour) &&
              (forexData.macdMinusOneHour < forexData.macdMinusTwoHours)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending macd - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending macd - go against")
            }

          }
          //                  }
        }
      }
    }

    println
    println("above low below high - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < 0.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > 0.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above low below high macd - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " above low below high macd - go against")
            }

          }
          //                  }
        }
      }
    }

    println
    println("below low above high macd - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd > 0.0)) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market) &&
              // (times.contains(forexData.time)) &&
              (forexData.macd < 0.0)) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " below low above high macd - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " below low above high macd - go against")
            }

          }
          //                  }
        }
      }
    }

    println
    println("trending - go against")
    for (market <- markets) {
      for (lossLimit <- 15 until 30 by 5) {
        for (profitLimit <- 10 until 30 by 5) {

          //          if ((lossLimit - 10) <= profitLimit) {

          var profit: Double = 0.0
          var winners: Int = 0
          var losers: Int = 0
          var not_sure: Int = 0

          for (forexData <- forexDataList) {

            if ((forexData.direction == "High") && (forexData.market == market)
//              &&
//              (times.contains(forexData.time))
            ) {

              if (forexData.priceHighOneHour > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowOneHour < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFourHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFourHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSixHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSixHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceHighEightHours > (forexData.price + lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceLowEightHours < (forexData.price - profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            } else if ((forexData.direction == "Low") && (forexData.market == market)
//              &&
//              (times.contains(forexData.time))
            ) {

              if (forexData.priceLowOneHour < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighOneHour > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowTwoHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighTwoHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowThreeHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighThreeHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFourHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFourHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowFiveHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighFiveHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSixHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSixHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowSevenHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighSevenHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else if (forexData.priceLowEightHours < (forexData.price - lossLimit)) {
                profit = profit - lossLimit
                losers = losers + 1
              } else if (forexData.priceHighEightHours > (forexData.price + profitLimit)) {
                profit = profit + profitLimit
                winners = winners + 1
              } else {
                not_sure = not_sure + 1
              }

            }

          }

          if ((profit > profitThreshold) && ((winners + losers) >= winnersPlusLosersThreshold)) {
            println("market = " + market + ", loss limit = " + lossLimit + ", profit limit = " + profitLimit + ", profit = " + profit +
              ", win = " + winners + ", loss = " + losers + ", not sure = " + not_sure)

            if (profitMap.contains(market)) {
              if (profit > profitMap.get(market).get._1) {
                profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending - go against")
              }
            } else {
              profitMap += market -> (profit, "loss limit = " + lossLimit + " profit limit = " + profitLimit + " trending - go against")
            }

          }

          //                  }

        }
      }
    }

    println
    println
    for ((k,v) <- profitMap) {
      println(k + " " + v._1 + " " + v._2)
    }

  }

  process

}