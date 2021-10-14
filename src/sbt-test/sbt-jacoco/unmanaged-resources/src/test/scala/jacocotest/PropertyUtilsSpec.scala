package jacocotest

import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class PropertyUtilsSpec extends AnyFlatSpec with Matchers {
	"PropertyUtils" should "load foo" in {
		PropertyUtils.getFoo() shouldBe 182
	}
}
