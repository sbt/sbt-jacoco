package jacocotest

import org.scalatest.flatspec._

class TestClassTests extends AnyFlatSpec {
  "TestClass" should "double a number correctly" in {
    // go through a companion value so the module actually loads:
    // scalac rewrites direct case-class apply calls to constructor calls
    val companion = TestClass
    assert(companion(2).double() === 4)
  }
}
