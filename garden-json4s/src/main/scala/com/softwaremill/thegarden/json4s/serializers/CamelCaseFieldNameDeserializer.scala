package com.softwaremill.thegarden.json4s.serializers

import com.softwaremill.thegarden.lawn.base.StringExtensions._
import org.json4s.FieldSerializer

object CamelCaseFieldNameDeserializer extends FieldSerializer[AnyRef](deserializer = {
  case (name, v) => (name.camelCase, v)
})
