package com.softwaremill.thegarden.shrubs.json4s

import org.scalatest.exceptions.TestFailedException

object Json4sHelpersManualTest extends App {

  Json4sTestHelpers.printingJsonOnFailure(
    """
      |{
      | "a": "foo",
      | "b": "bar"
      |}
    """.stripMargin) {
    json =>
      throw new TestFailedException(3)
  }
}
