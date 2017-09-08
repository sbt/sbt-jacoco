package jacocotest.extra

import java.util.{Currency, Locale}

case class Money(amount: BigDecimal, currency: Currency) {
  override def toString: String = {
    "%.2f %s".format(amount, currency.getSymbol(Locale.forLanguageTag("en-GB")))
  }
}
