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

object Loan {
  def loan[A <: AutoCloseable](resource: A) = new Loan(resource)
}
