package com.softwaremill.thegarden.json4s.serializers

import org.json4s.FieldSerializer

import Inflector._

object UnderscorizeFieldNamesSerializer extends FieldSerializer[AnyRef](serializer = {
  case (name, v) => Some((name.underscore, v))
})
