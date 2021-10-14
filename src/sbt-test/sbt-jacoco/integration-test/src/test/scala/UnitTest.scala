package example

import org.scalatest.funsuite._

class UnitTest extends AnyFunSuite {
  test("forUnitTests") {
    assert(TestSubject.forUnitTests == 2)
  }
}
