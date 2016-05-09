package edu.columbia.pm.dashboard.data

case class Holding(holdingKey: HoldingKey, quantity: Option[Long] = None, price: Option[Double] = None /*SoD*/)

object Holding {
  implicit def long2Option(quantity: Long) = Option(quantity)

  implicit def int2LongOption(quantity: Int) = Option(quantity.toLong)
  implicit def double2Option(price: Double) = Option(price)
  implicit def int2DoubleOption(price: Int) = Option(price.toDouble)
}

case class HoldingDetails(accountId: String, productId: String, quantity: Long)

case class HoldingKey(accountId: String, productId: String)

case class Trade(tradeId: Long, holdingKey: HoldingKey, quantity: Long)
