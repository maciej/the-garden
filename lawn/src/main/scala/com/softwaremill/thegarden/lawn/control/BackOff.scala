package com.softwaremill.thegarden.lawn.control

import scala.concurrent.duration.Duration

trait BackOffAlgorithm {

  protected val retries: Int
  protected val duration: Duration

  protected def backingOff: BackOff

  def backOff: Option[BackOff] = {
    if (retries <= 0) {
      None
    } else {
      Some(backingOff)
    }
  }
}

case class BackOff(algo: BackOffAlgorithm, duration: Duration) {
  def sleep(): Option[BackOff] = {
    Thread.sleep(duration.toMillis)
    algo.backOff
  }
}

object BackOff extends ((BackOffAlgorithm, Duration) => BackOff) {

  private class StandardBackOffAlgorithm(override protected val duration: Duration,
                                         override protected val retries: Int) extends BackOffAlgorithm {

    override protected def backingOff = BackOff(new StandardBackOffAlgorithm(duration, retries - 1), duration)
  }

  private class ExponentialBackOffAlgorithm(override protected val duration: Duration,
                                            override protected val retries: Int) extends BackOffAlgorithm {

    override protected def backingOff = BackOff(new ExponentialBackOffAlgorithm(duration * 2, retries - 1), duration)
  }

  def linear(initialDuration: Duration, retries: Int): BackOff =
    BackOff(new StandardBackOffAlgorithm(initialDuration, retries), initialDuration)

  def exponential(initialDuration: Duration, retries: Int) : BackOff =
    BackOff(new ExponentialBackOffAlgorithm(initialDuration, retries), initialDuration)

}
