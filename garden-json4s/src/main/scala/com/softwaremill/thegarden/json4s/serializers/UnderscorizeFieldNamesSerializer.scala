package com.softwaremill.thegarden.json4s.serializers

import org.json4s.FieldSerializer

object UnderscorizeFieldNamesSerializer extends FieldSerializer[AnyRef](serializer = {
  case (name, v) => Some((Underscorize.transform(name), v))
})

private[serializers] object Underscorize {
  def transform(word: String): String = {
    val spacesPattern = "[-\\s]".r
    val firstPattern = "([A-Z]+)([A-Z][a-z])".r
    val secondPattern = "([a-z\\d])([A-Z])".r
    val replacementPattern = "$1_$2"
    spacesPattern.replaceAllIn(
      secondPattern.replaceAllIn(
        firstPattern.replaceAllIn(
          word, replacementPattern), replacementPattern), "_").toLowerCase
  }
}
