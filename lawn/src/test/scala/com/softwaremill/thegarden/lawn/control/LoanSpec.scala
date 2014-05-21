package com.softwaremill.thegarden.lawn.control

import org.scalatest.{ShouldMatchers, FlatSpec}


import com.softwaremill.thegarden.lawn.control.Loan.loan
import java.util.concurrent.atomic.AtomicBoolean

class LoanSpec extends FlatSpec with ShouldMatchers {

  it should "close a resource if the block throws an exception" in {
    val closeableOnce = new CloseableOnce

    try {
      loan(closeableOnce) to { closeable =>
        throw new RuntimeException("Foo !")
      }
    } catch {
      case e: RuntimeException =>
    } finally {
      closeableOnce.closed.get() shouldBe true
    }
  }

  it should "rethrow the same exception what was thrown in the block" in {
    val closeableOnce = new CloseableOnce
    val thrownException = new RuntimeException("Foo bar!")

    var caughtException = false

    try {
      loan(closeableOnce) to { closeable =>
        throw thrownException
      }
    } catch {
      case e: RuntimeException =>
        caughtException = true
        e shouldEqual thrownException
    } finally {
      caughtException shouldBe true
    }
  }

}


class CloseableOnce extends AutoCloseable {

  val closed = new AtomicBoolean(false)

  override def close() = {
    closed.set(true)
  }

}