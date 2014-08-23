package com.softwaremill.thegarden.lawn.util

import java.util.{UUID => JavaUUID}

case class UUIDString(value : String) extends AnyVal

object UUIDString extends ((String) => UUIDString) {
  def apply() = JavaUUID.randomUUID().toString
}
