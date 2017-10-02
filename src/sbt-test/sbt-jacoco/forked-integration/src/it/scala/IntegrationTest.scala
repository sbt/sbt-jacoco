package example

import org.scalatest._

class UnitTest extends FunSuite {
  test("forIntegrationTests") {
    assert(TestSubject.forIntegrationTests == 4)
  }
}