package example

import org.scalatest.funsuite._

class UnitTest extends AnyFunSuite {
  test("forIntegrationTests") {
    assert(TestSubject.forIntegrationTests == 4)
  }
}
