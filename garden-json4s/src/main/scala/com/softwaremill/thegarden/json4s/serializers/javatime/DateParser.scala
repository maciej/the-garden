package com.softwaremill.thegarden.json4s.serializers.javatime

import org.json4s.{MappingException, Formats}

/**
 * @author Maciej Bilas
 * @since 8/3/15 15:34
 */
object DateParser {
  def parse(s: String, format: Formats) =
    format.dateFormat.parse(s).map(_.getTime)
      .getOrElse(throw new MappingException(s"Invalid date format $s"))
}