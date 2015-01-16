package com.softwaremill.thegarden.json4s.serializers

import scala.language.implicitConversions

/**
 * @author Maciej Bilas
 * @since 16/1/15 18:01
 */
private[serializers] object Inflector {
  implicit def enrichString(str: String): RichString = new RichString(str)
}

private[serializers] class RichString(underlying: String) {

  def underscore: String = {
    val spacesPattern = "[-\\s]".r
    val firstPattern = "([A-Z]+)([A-Z][a-z])".r
    val secondPattern = "([a-z\\d])([A-Z])".r
    val replacementPattern = "$1_$2"
    spacesPattern.replaceAllIn(
      secondPattern.replaceAllIn(
        firstPattern.replaceAllIn(
          underlying, replacementPattern), replacementPattern), "_").toLowerCase
  }

  def camelCase: String = "_([a-z\\d])".r.replaceAllIn(underlying, { m =>
    m.group(1).toUpperCase
  })

}
