package com.softwaremill.thegarden.json4s.serializers.javatime

import java.util.Date
import java.time.{Instant, ZoneId, LocalDateTime}

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
 * @author Maciej Bilas
 * @since 8/3/15 15:34
 */
case object LocalDateTimeSerializer extends CustomSerializer[LocalDateTime](format => ( {
  case JString(s) => LocalDateTime.ofInstant(Instant.ofEpochMilli(DateParser.parse(s, format)),
    ZoneId.systemDefault())
  case JNull => null
}, {
  case d: LocalDateTime =>
    val ldtAsDate = Date.from(d.atZone(ZoneId.systemDefault()).toInstant)
    JString(format.dateFormat.format(ldtAsDate))
}
  )
)