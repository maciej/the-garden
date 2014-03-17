package com.softwaremill.thegarden.lawn.timing

import org.joda.time.Duration

trait Timing {

  // http://stackoverflow.com/questions/9160001/how-to-profile-methods-in-scala
  def timePrintingResult[R](text: String = "Elapsed time")(block: => R): R = {
    val (result, duration) = timeReturningDuration(block)
    val nf = java.text.NumberFormat.getIntegerInstance(java.util.Locale.ROOT)
    nf.setGroupingUsed(true)
    println(text + ": " + nf.format(duration.getMillis) + "ms")
    result
  }

  def timeReturningDuration[R](block: => R) : (R, Duration) = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    (result, new Duration((t1 - t0) / 1000 / 1000))
  }

}

object Timing extends Timing
