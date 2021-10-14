package jacocotest

import org.scalatest.flatspec._

class TestClassTests extends AnyFlatSpec
{
	"TestClass" should "double a number correctly" in
	{
	    assert( new TestClass( 2 ).double === 4 )
	}
}
