package com.softwaremill.thegarden.json4s.serializers

import org.json4s.FieldSerializer

import Inflector._

object CamelCaseFieldNameDeserializer extends FieldSerializer[AnyRef](deserializer = {
  case (name, v) => (name.camelCase, v)
})
