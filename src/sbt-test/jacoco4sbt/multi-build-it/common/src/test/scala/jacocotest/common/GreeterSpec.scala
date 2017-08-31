package jacocotest.common

import org.scalatest._

class GreeterSpec extends FlatSpec with Matchers {
  val greeter = new Greeter()

  "greet" should "greet bob" in {
    greeter.greet("bob") shouldBe "Hello, bob"
  }
}
