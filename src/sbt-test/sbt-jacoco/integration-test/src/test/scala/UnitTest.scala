package example

import org.scalatest.funsuite.*

class UnitTest extends AnyFunSuite {
  test("forUnitTests") {
    assert(TestSubject.forUnitTests == 2)
  }
}
