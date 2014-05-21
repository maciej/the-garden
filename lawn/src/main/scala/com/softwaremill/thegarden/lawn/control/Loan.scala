package com.softwaremill.thegarden.lawn.control

import scala.util.control.NonFatal

/*
 * Taken from http://illegalexception.schlichtherle.de/2012/07/19/try-with-resources-for-scala/
 * and modified
 */
class Loan[A <: AutoCloseable](resource: A) {
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
class LoanWithCloser[A](resource: A, closeFun: () => Unit) {
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
            closeFun()
          } catch {
            case NonFatal(y) => t.addSuppressed(y)
          }
        } else {
          closeFun()
        }
      }
    }
  }
}

object Loan {
  def loan[A <: AutoCloseable](resource: A) = new Loan(resource)

  def loanAny[A <: {def close() : Unit}](resource: A) = new LoanWithCloser(resource, resource.close)

  def loanWithCloser[A](resource: A)(closeFun: A => Unit) = new LoanWithCloser(resource, () => {closeFun(resource)})
}
