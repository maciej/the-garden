package com.softwaremill.thegarden.json4s.serializers

import org.json4s.FieldSerializer
import com.softwaremill.thegarden.lawn.base.StringExtensions._

object UnderscorizeFieldNamesSerializer extends FieldSerializer[AnyRef](serializer = {
  case (name, v) => Some((name.underscore, v))
})
