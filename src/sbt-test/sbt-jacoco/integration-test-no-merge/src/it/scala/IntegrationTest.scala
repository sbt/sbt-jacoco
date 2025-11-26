package example

import org.scalatest.funsuite.*

class UnitTest extends AnyFunSuite {
  test("forIntegrationTests") {
    assert(TestSubject.forIntegrationTests == 4)
  }
}
