package jacocotest

import org.scalatest.FlatSpec

class TestClassTests extends FlatSpec
{
	"TestClass" should "double a number correctly" in
	{
	    assert( new TestClass( 2 ).double === 4 )
	}
}