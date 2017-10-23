package jacocotest

import org.scalatest.{FlatSpec, Matchers}

class PropertyUtilsSpec extends FlatSpec with Matchers {
	"PropertyUtils" should "load foo" in {
		PropertyUtils.getFoo() shouldBe 182
	}
}