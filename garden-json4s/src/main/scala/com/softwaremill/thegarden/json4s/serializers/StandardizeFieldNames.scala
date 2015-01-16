package com.softwaremill.thegarden.json4s.serializers

import org.json4s.FieldSerializer

object StandardizeFieldNames extends FieldSerializer[AnyRef](
  deserializer = CamelCaseFieldNameDeserializer.deserializer,
  serializer = UnderscorizeFieldNamesSerializer.serializer
)
