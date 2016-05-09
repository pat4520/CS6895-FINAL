package edu.columbia.pm.dashboard.cache

import edu.columbia.pm.dashboard.data.{Holding, HoldingKey}
import edu.columbia.pm.dashboard.data.Holding._

import scala.collection.mutable

object Cache {

  val accounts = Map(
    "C3P0" -> "Science Center",
    "A1B2C3D4" -> "A1 Pension Fund",
    "A11B22C33" -> "XYZ Personal Account"
  )

  val products = Map(
    "MSFT" -> "Microsoft Common Stock",
    "BRK-B" -> "Berkshire Hathaway B",
    "CVC" -> "Cablevision Systems Co A",
    "KO" -> "Coca-Cola Co",
    "GS" -> "Goldman Sachs Group Inc",
    "IBM" -> "IBM Common Stock",
    "AA" -> "Alcoa Inc",
    "AMZN" -> "Amazon.com Inc",
    "V" -> "Visa Inc",
    "SBUX" -> "Starbucks Corp",
    "QCOM" -> "QUALCOMM Inc"
  )

  val prices = mutable.Map(
    "MSFT" -> 0.0,
    "BRK-B" -> 0.0,
    "CVC" -> 0.0,
    "KO" -> 0.0,
    "GS" -> 0.0,
    "IBM" -> 0.0,
    "AA" -> 0.0,
    "AMZN" -> 0.0,
    "V" -> 0.0,
    "SBUX" -> 0.0,
    "QCOM" -> 0.0
  )

  val preditions = mutable.Map(
    "MSFT" -> 0.0,
    "BRK-B" -> 0.0,
    "CVC" -> 0.0,
    "KO" -> 0.0,
    "GS" -> 0.0,
    "IBM" -> 0.0,
    "AA" -> 0.0,
    "AMZN" -> 0.0,
    "V" -> 0.0,
    "SBUX" -> 0.0,
    "QCOM" -> 0.0
  )

  val sectors = Map(
    "MSFT" -> "Tech",
    "BRK-B" -> "Finance",
    "CVC" -> "Services",
    "KO" -> "Commodities",
    "GS" -> "Finance",
    "IBM" -> "Tech",
    "AA" -> "Services",
    "AMZN" -> "Tech",
    "V" -> "Finance",
    "SBUX" -> "Services",
    "QCOM" -> "Tech"
  )

  val sentiments = mutable.Map(
    "MSFT" -> 5.0,
    "BRK-B" -> 5.0,
    "CVC" -> 5.0,
    "KO" -> 5.0,
    "GS" -> 5.0,
    "IBM" -> 5.0,
    "AA" -> 5.0,
    "AMZN" -> 5.0,
    "V" -> 5.0,
    "SBUX" -> 5.0,
    "QCOM" -> 5.0
  )

  val portfolio = Map(1 ->
    Seq(
      Holding(HoldingKey("C3P0", "MSFT"), 100, 0.0),
      Holding(HoldingKey("C3P0", "BRK-B"), 170, 0.0),
      Holding(HoldingKey("C3P0", "CVC"), 300, 0.0),
      Holding(HoldingKey("C3P0", "KO"), 425, 0.0),
      Holding(HoldingKey("C3P0", "GS"), 500, 0.0),

      Holding(HoldingKey("A1B2C3D4", "MSFT"), 100, 0.0),
      Holding(HoldingKey("A1B2C3D4", "BRK-B"), 100, 0.0),
      Holding(HoldingKey("A1B2C3D4", "CVC"), 100, 0.0),
      Holding(HoldingKey("A1B2C3D4", "KO"), 100, 0.0),
      Holding(HoldingKey("A1B2C3D4", "IBM"), 100, 0.0),

      Holding(HoldingKey("A11B22C33", "MSFT"), 50, 0.0),
      Holding(HoldingKey("A11B22C33", "AA"), 50, 0.0),
      Holding(HoldingKey("A11B22C33", "AMZN"), 50, 0.0),
      Holding(HoldingKey("A11B22C33", "V"), 50, 0.0),
      Holding(HoldingKey("A11B22C33", "SBUX"), 50, 0.0),
      Holding(HoldingKey("A11B22C33", "QCOM"), 50, 0.0)
    )
  )

  def getHoldingKeys(portfolioId: Int) = portfolio.getOrElse(portfolioId, Seq.empty).map(_.holdingKey)

  def getHoldings(productId: String) = portfolio.values.flatten.filter(_.holdingKey.productId == productId)

  def getHoldingKeys(productId: String) = getHoldings(productId).collect { case holding => holding.holdingKey }

  def getHoldings(portfolioId: Int) = portfolio.getOrElse(portfolioId, Seq.empty)

}

