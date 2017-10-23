package jacocotest

import java.{util => ju}

object PropertyUtils {
  private val props = new ju.Properties()
  props.load(getClass.getResourceAsStream("/info.properties"))

  def getFoo(): Int = {
    props.getProperty("foo").toInt
  }
}
