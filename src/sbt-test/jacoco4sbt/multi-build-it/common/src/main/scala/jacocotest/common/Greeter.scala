package jacocotest.common

object Greeter {
  val instance = new Greeter()
}

class Greeter {
  def greet(name: String): String = s"Hello, $name"

  def defaultGreeting: String = greet("world")
}
