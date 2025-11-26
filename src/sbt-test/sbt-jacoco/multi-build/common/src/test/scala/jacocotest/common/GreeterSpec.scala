package jacocotest.common

import org.scalatest.flatspec.*
import org.scalatest.matchers.should.Matchers

class GreeterSpec extends AnyFlatSpec with Matchers {
  val greeter = new Greeter()

  "greet" should "greet bob" in {
    greeter.greet("bob") shouldBe "Hello, bob"
  }
}
