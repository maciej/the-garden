package com.softwaremill.thegarden.shrubs.matchers

import org.joda.time.DateTime
import org.scalatest.matchers.{MatchResult, Matcher}

object DateTimeMatchers {

  def beAfter(right: DateTime) = new Matcher[DateTime] {
    def apply(left: DateTime) = MatchResult(left.isAfter(right),
      s"$left is not after $right",
      s"$left is not before $right"
    )
  }

  def beBefore(right: DateTime) = new Matcher[DateTime] {
    def apply(left: DateTime) = MatchResult(left.isBefore(right),
      s"$left is not before $right",
      s"$left is not after $right"
    )
  }
}
