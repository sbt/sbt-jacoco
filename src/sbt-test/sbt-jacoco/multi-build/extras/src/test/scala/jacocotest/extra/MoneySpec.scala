package jacocotest.extra

import java.util.Currency
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class MoneySpec extends AnyFlatSpec with Matchers {
  "toString" should "include currency" in {
    Money(BigDecimal(5), Currency.getInstance("GBP")).toString shouldBe "5.00 £"
  }
}
