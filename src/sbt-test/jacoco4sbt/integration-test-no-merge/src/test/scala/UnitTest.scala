package example

import org.scalatest._

class UnitTest extends FunSuite {
  test("forUnitTests") {
    assert(TestSubject.forUnitTests == 2)
  }
}