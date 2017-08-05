package com.betfair.robots

import java.math.{BigDecimal, RoundingMode}

/**
  * This class rounds a price either <code>UP</code> or <code>DOWN</code> to the
  * nearest valid Betfair price.
  *
  * @author geraint.jones
  *
  */
object ShortPriceFormat {

  /**
    * For a given <code>DIRECTION</code>, round the supplied price to the nearest valid Betfair price.
    *
    * @param price the price to be rounded
    * @return the rounded price
    */
  def round(price: Double): Double = {

    var newPrice = BigDecimal.valueOf(price)

    if (price > 6.0) {
      newPrice = newPrice.setScale(2, RoundingMode.HALF_EVEN)
      while (newPrice.remainder(BigDecimal.valueOf(0.2)).doubleValue() > 0) {
        newPrice = newPrice.add(BigDecimal.valueOf(0.01))
      }
    } else if (price > 4.0) {
      newPrice = newPrice.setScale(2, RoundingMode.HALF_EVEN)
      while (newPrice.remainder(BigDecimal.valueOf(0.1)).doubleValue() > 0) {
        newPrice = newPrice.add(BigDecimal.valueOf(0.01))
      }
    } else if (price > 3.0) {
      newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN)
      while (newPrice.remainder(BigDecimal.valueOf(0.05)).doubleValue() > 0) {
        newPrice = newPrice.add(BigDecimal.valueOf(0.001))
      }
    } else if (price > 2.0) {
      newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN)
      while (newPrice.remainder(BigDecimal.valueOf(0.02)).doubleValue() > 0) {
        newPrice = newPrice.add(BigDecimal.valueOf(0.001))
      }
    } else {
      newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN)
      while (newPrice.remainder(BigDecimal.valueOf(0.01)).doubleValue() > 0) {
        newPrice = newPrice.add(BigDecimal.valueOf(0.001))
      }
    }
    newPrice.doubleValue()
  }

}