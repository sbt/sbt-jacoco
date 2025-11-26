package jacocotest

import org.scalatest.flatspec.*
import org.scalatest.matchers.should.Matchers

class PropertyUtilsSpec extends AnyFlatSpec with Matchers {
  "PropertyUtils" should "load foo" in {
    PropertyUtils.getFoo() shouldBe 182
  }
}
