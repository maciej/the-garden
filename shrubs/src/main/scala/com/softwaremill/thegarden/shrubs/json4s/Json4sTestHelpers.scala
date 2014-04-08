package com.softwaremill.thegarden.shrubs.json4s

import org.json4s.JsonAST.JValue
import org.scalatest.exceptions.TestFailedException
import org.json4s.jackson.JsonMethods._
import org.json4s.StringInput

object Json4sTestHelpers {

  def printingJsonOnFailure(json: JValue)(block: (JValue) => Unit) {
    try {
      block(json)
    } catch {
      case e: TestFailedException =>
        Console.err.println(pretty(json))
        throw e
    }
  }

  def printingJsonOnFailure(json: String)(block: (JValue) => Unit) : Unit =
    printingJsonOnFailure(parse(StringInput(json)))(block)
}

