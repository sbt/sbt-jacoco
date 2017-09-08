package jacocotest.extra

import java.util.Currency
import org.scalatest._

class MoneySpec extends FlatSpec with Matchers {
  "toString" should "include currency" in {
    Money(BigDecimal(5), Currency.getInstance("GBP")).toString shouldBe "5.00 £"
  }
}
