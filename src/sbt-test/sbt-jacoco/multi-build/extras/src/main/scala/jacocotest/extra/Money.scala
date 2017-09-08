package jacocotest.extra

import java.util.Currency

case class Money(amount: BigDecimal, currency: Currency) {
  override def toString: String = {
    "%.2f %s".format(amount, currency.getSymbol)
  }
}
