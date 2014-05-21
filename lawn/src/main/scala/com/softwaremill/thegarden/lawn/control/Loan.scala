package com.softwaremill.thegarden.lawn.control

import scala.util.control.NonFatal

/*
 * Taken from http://illegalexception.schlichtherle.de/2012/07/19/try-with-resources-for-scala/
 * and modified
 */
class Loan[A <: AutoCloseable](resource: A) extends BaseLoan[A] {
  def to[B](block: A => B) = {
    var t: Throwable = null
    try {
      block(resource)
    } catch {
      case NonFatal(x) => t = x; throw x
    } finally {
      if (resource != null) {
        if (t != null) {
          try {
            resource.close()
          } catch {
            case NonFatal(y) => t.addSuppressed(y)
          }
        } else {
          resource.close()
        }
      }
    }
  }
}

/* Structural typed version of Loan. I hate this () code repetition */
class AnyLoan[A <: {def close() : Unit}](resource: A) extends BaseLoan[A] {
  def to[B](block: A => B) = {
    var t: Throwable = null
    try {
      block(resource)
    } catch {
      case NonFatal(x) => t = x; throw x
    } finally {
      if (resource != null) {
        if (t != null) {
          try {
            resource.close()
          } catch {
            case NonFatal(y) => t.addSuppressed(y)
          }
        } else {
          resource.close()
        }
      }
    }
  }
}

trait BaseLoan[A] {
  def to[B](block: A => B)
}

object Loan {
  def loan[A <: AutoCloseable](resource: A) = new Loan(resource)

  def loanAny[A <: {def close() : Unit}](resource: A) = new AnyLoan(resource)
}
