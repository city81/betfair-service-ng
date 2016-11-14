package com.betfair.robots.racing

import scala.collection.immutable.ListMap

object ProcessPlacepot extends App {

  def process() = {

    //
    // load data into a list
    //
    val placepotList = scala.collection.mutable.Map[String, PlacepotRaceDetail]()
    val runnersList = scala.collection.mutable.Map[String, PlacepotRaceDetail]()

    val resultMap = scala.collection.mutable.Map[String, Double]()

    val profitThreshold = 0.0

    val fileLines = io.Source.fromFile("/Users/geraint/Documents/placepot.csv").getLines.drop(1).toList

    for (fileLine <- fileLines) {

      var fileLineItems = fileLine.split(",")

      // ignore lines without a Y or N
      if (fileLineItems.size == 8) {

        var key = fileLineItems(0) + "-" + fileLineItems(1) + "-" + fileLineItems(3) + "-" + fileLineItems(2) + "-" + fileLineItems(4)

        if (placepotList.contains(key)) {

          var placepotDetail = placepotList.get(key).get

          if (fileLineItems(6).equals("Postdata")) placepotDetail.postdataSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("Postdata")) placepotDetail.postdataSelectionPlaced = Some(fileLineItems(7))
          if (fileLineItems(6).equals("RP Ratings")) placepotDetail.rpRatingsSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("RP Ratings")) placepotDetail.rpRatingsSelectionPlaced = Some(fileLineItems(7))
          if (fileLineItems(6).equals("Topspeed")) placepotDetail.topspeedSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("Topspeed")) placepotDetail.topspeedSelectionPlaced = Some(fileLineItems(7))

          placepotList += (key -> placepotDetail)
        } else {
          var placepotDetail = new PlacepotRaceDetail(
            if (fileLineItems(6).equals("Postdata")) Some(fileLineItems(5)) else None,
            if (fileLineItems(6).equals("Postdata")) Some(fileLineItems(7)) else None,
            if (fileLineItems(6).equals("RP Ratings")) Some(fileLineItems(5)) else None,
            if (fileLineItems(6).equals("RP Ratings")) Some(fileLineItems(7)) else None,
            if (fileLineItems(6).equals("Topspeed")) Some(fileLineItems(5)) else None,
            if (fileLineItems(6).equals("Topspeed")) Some(fileLineItems(7)) else None
          )
          placepotList += (key -> placepotDetail)
        }
      } else {
        var key = fileLineItems(0) + "-" + fileLineItems(1) + "-" + fileLineItems(3) + "-" + fileLineItems(2) + "-" + fileLineItems(4)

        if (runnersList.contains(key)) {

          var placepotDetail = runnersList.get(key).get

          if (fileLineItems(6).equals("Postdata")) placepotDetail.postdataSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("Postdata")) placepotDetail.postdataSelectionPlaced = None
          if (fileLineItems(6).equals("RP Ratings")) placepotDetail.rpRatingsSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("RP Ratings")) placepotDetail.rpRatingsSelectionPlaced = None
          if (fileLineItems(6).equals("Topspeed")) placepotDetail.topspeedSelection = Some(fileLineItems(5))
          if (fileLineItems(6).equals("Topspeed")) placepotDetail.topspeedSelectionPlaced = None

          runnersList += (key -> placepotDetail)
        } else {
          var placepotDetail = new PlacepotRaceDetail(
            if (fileLineItems(6).equals("Postdata")) Some(fileLineItems(5)) else None,
            None,
            if (fileLineItems(6).equals("RP Ratings")) Some(fileLineItems(5)) else None,
            None,
            if (fileLineItems(6).equals("Topspeed")) Some(fileLineItems(5)) else None,
            None
          )
          runnersList += (key -> placepotDetail)
        }
      }
    }

    println()

    val raceTypes = Vector("Hcp", "", "Mdn", "Claimer")


    // three places - rp and post - same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.postdataSelectionPlaced.isDefined &&
                  placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - rp and post - same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - rp and post - same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - rp and post - same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.postdataSelectionPlaced.isDefined &&
                  placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - rp and post - same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - rp and post - same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - rp and post - same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.postdataSelectionPlaced.isDefined &&
                  placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - rp and post - same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - rp and post - same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }



    // three places - rp and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - rp and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - rp and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - post and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }



    // three places - post and rp - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post and rp - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post and rp - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post and rp - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post and rp - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post and rp - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post and rp - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post and rp - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post and rp - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - post and top - same - rp - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post and top - same - rp - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post and top - same - rp - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post and top - same - rp - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post and top - same - rp - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post and top - same - rp - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post and top - same - rp - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post and top - same - rp - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post and top - same - rp - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }


    // three places - rp and top - same - post - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - rp and top - same - post - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - rp and top - same - post - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - rp and top - same - post - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - rp and top - same - post - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - rp and top - same - post - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - rp and top - same - post - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - rp and top - same - post - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - rp and top - same - post - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - rp and post - same - top - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - rp and post - same - top - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - rp and post - same - top - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - rp and post - same - top - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - rp and post - same - top - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - rp and post - same - top - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - rp and post - same - top - diff
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))
              ) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - rp and post - same - top - diff - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - rp and post - same - top - diff - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - post and rp and top - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post and rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post and rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post and rp - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post and rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post and rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post and rp - different
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get != placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post and rp and top - different - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post and rp and top - different - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - post only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - rp only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - rp only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - rp only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - rp only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - rp only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - rp only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - rp only
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.rpRatingsSelection.isDefined &&
              !placepotDetail.get.postdataSelection.isDefined &&
              !placepotDetail.get.topspeedSelection.isDefined) {
              if ((placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - rp only - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - rp only - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // three places - post and rp and top - all same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners > 7) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("three places - post and rp and top - all same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("three places - post and rp and top - all same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // two places - post and rp and top - all same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if ((runners > 4) && (runners < 8)) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("two places - post and rp and top - all same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("two places - post and rp and top - all same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // one place - post and rp and top - all same
    for (raceType <- raceTypes) {
      println()
      var winners = 0
      var selections = 0
      for (placepotDetailKey <- placepotList.keys) {
        var detailRacetype = placepotDetailKey.split("-")(3)
        var runners = placepotDetailKey.split("-")(4).toInt
        var placepotDetail = placepotList.get(placepotDetailKey)
        if (detailRacetype.equals(raceType)) {
          if (runners < 5) {
            if (placepotDetail.get.postdataSelection.isDefined &&
              placepotDetail.get.rpRatingsSelection.isDefined &&
              placepotDetail.get.topspeedSelection.isDefined &&
              placepotDetail.get.postdataSelection.get == placepotDetail.get.rpRatingsSelection.get &&
              placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
              placepotDetail.get.topspeedSelection.get == placepotDetail.get.postdataSelection.get) {
              if ((placepotDetail.get.postdataSelectionPlaced.isDefined &&
                placepotDetail.get.postdataSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.rpRatingsSelectionPlaced.isDefined &&
                  placepotDetail.get.rpRatingsSelectionPlaced.get.equals("Y")) ||
                (placepotDetail.get.topspeedSelectionPlaced.isDefined &&
                  placepotDetail.get.topspeedSelectionPlaced.get.equals("Y"))) {
                winners = winners + 1
              }
              selections = selections + 1
            }
          }
        }
      }
      if (true) {
        println("one place - post and rp and top - all same - " + raceType)
        println(winners + " / " + selections + " = " + (winners.toDouble / selections))
        if ((selections >= 5) && ((winners.toDouble / selections) >= 0.75)) {
          resultMap += ("one place - post and rp and top - all same - " + raceType -> (winners.toDouble / selections))
        }
      }
    }

    // sort result map
    println()
    resultMap.toSeq.sortBy(_._2).reverse.foreach {
        println
    }

    println
    println

    // iterate over the runners list and map to the resultMap keys
    val orderedMap = ListMap(runnersList.toSeq.sortBy(_._1):_*)

    for (runnerListKey <- orderedMap.keys) {
      println(runnerListKey)
      var placepotDetail = runnersList.get(runnerListKey)
      var runners = runnerListKey.split("-")(4).toInt
      var buildKey = ""

      if (runners < 5) {
        buildKey += "one place - "
      } else if ((runners > 4) && (runners < 8)) {
        buildKey += "two places - "
      } else {
        buildKey += "three places - "
      }

      if (placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.postdataSelection.isDefined &&
        !placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get) {
        buildKey += "rp and post - same - "
      } else if (placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        !placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
        buildKey += "rp and top - different - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        !placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.postdataSelection.get != placepotDetail.get.topspeedSelection.get) {
        buildKey += "post and top - different - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        !placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
        buildKey += "post and rp - different - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.postdataSelection.get == placepotDetail.get.topspeedSelection.get &&
        placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get) {
        buildKey += "post and top - same - rp - diff - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
        placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.postdataSelection.get) {
        buildKey += "rp and top - same - post - diff - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.postdataSelection.get &&
        placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get) {
        buildKey += "rp and post - same - top - diff - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.postdataSelection.get != placepotDetail.get.rpRatingsSelection.get &&
        placepotDetail.get.rpRatingsSelection.get != placepotDetail.get.topspeedSelection.get &&
        placepotDetail.get.topspeedSelection.get != placepotDetail.get.postdataSelection.get) {
        buildKey += "post and rp and top - different - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        placepotDetail.get.rpRatingsSelection.isDefined &&
        placepotDetail.get.topspeedSelection.isDefined &&
        placepotDetail.get.postdataSelection.get == placepotDetail.get.rpRatingsSelection.get &&
        placepotDetail.get.rpRatingsSelection.get == placepotDetail.get.topspeedSelection.get &&
        placepotDetail.get.topspeedSelection.get == placepotDetail.get.postdataSelection.get) {
        buildKey += "post and rp and top - all same - "
      } else if (placepotDetail.get.postdataSelection.isDefined &&
        !placepotDetail.get.rpRatingsSelection.isDefined &&
        !placepotDetail.get.topspeedSelection.isDefined) {
        buildKey += "post only - "
      } else if (placepotDetail.get.rpRatingsSelection.isDefined &&
        !placepotDetail.get.postdataSelection.isDefined &&
        !placepotDetail.get.topspeedSelection.isDefined) {
        buildKey += "rp only - "
      } else {
        buildKey += "not found"
      }

      buildKey += runnerListKey.split("-")(3)

      println(buildKey)

      if (resultMap.contains(buildKey)) {
        println(resultMap.get(buildKey))
      } else {
        println("not found")
      }

      println

    }


  }

  process

}