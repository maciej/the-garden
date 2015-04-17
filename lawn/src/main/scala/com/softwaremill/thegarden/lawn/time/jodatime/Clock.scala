package com.softwaremill.thegarden.lawn.time.jodatime

import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

trait Clock {
  def now: LocalDateTime
  def now(dtz: DateTimeZone): DateTime
}

object Clock {
  implicit val defaultClock = RealClock
}

object RealClock extends Clock {
  def now: LocalDateTime = LocalDateTime.now()
  def now(dtz: DateTimeZone): DateTime = DateTime.now(dtz)
}

class FakeClock(val now: LocalDateTime) extends Clock {
  def now(dtz: DateTimeZone): DateTime = now.toDateTime(dtz)
}

object FakeClock {
  def withTime[T](time: LocalDateTime)(fun: Clock => T) = fun(new FakeClock(time))
}
