package jacocotest.common

import org.scalatest._

class GreeterIntegrationSpec extends FlatSpec with Matchers {
  "defaultGreeting" should "greet world" in {
    Greeter.instance.defaultGreeting shouldBe "Hello, world"
  }
}
