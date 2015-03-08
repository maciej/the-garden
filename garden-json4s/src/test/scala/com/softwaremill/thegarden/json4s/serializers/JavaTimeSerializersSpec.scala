package com.softwaremill.thegarden.json4s.serializers

import java.time.{LocalTime, LocalDate, LocalDateTime}

import org.json4s.{TypeHints, NoTypeHints, DefaultFormats}
import org.scalatest.{Matchers, FlatSpec}

import org.json4s.jackson.{Serialization => s}

class JavaTimeSerializersSpec extends FlatSpec with Matchers {

  implicit val defaultFormats = new DefaultFormats {
    override def dateFormatter = {
      new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'")
    }
    override val typeHints: TypeHints = NoTypeHints
  } ++ JavaTimeSerializers.all

  val FirstDayOf2015Midday = LocalDateTime.of(
    LocalDate.of(2015, 1, 1), LocalTime.of(12, 0, 0, 0))

  val FirstDayOf2015MiddayStr = "2015-01-01 12:00:00Z"

  it should "serialize a LocalDateTime" in {
    val serialized = s.write(FirstDayOf2015Midday)

    serialized shouldEqual "\"" + FirstDayOf2015MiddayStr + "\""
  }

  it should "serialize a LocalDateTime inside a holder" in {
    val serialized = s.write(LocalDateTimeHolder(FirstDayOf2015Midday))

    serialized shouldEqual s"""{"dt":"$FirstDayOf2015MiddayStr"}"""
  }

}

private[serializers] case class LocalDateTimeHolder(dt : LocalDateTime)
