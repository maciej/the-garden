package com.softwaremill.thegarden.lawn.control

import org.scalatest.{ShouldMatchers, FlatSpec}

import com.softwaremill.thegarden.lawn.control.Loan.{loan, loanAny, loanWithCloser}
import java.util.concurrent.atomic.{AtomicInteger, AtomicBoolean}

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

  it should "close a structurally typed resource if even if the block throws an exception" in {
    val closeableOnce = new CloseableOnce

    try {
      loanAny(closeableOnce) to { closeable =>
        throw new RuntimeException("Foo !")
      }
    } catch {
      case e: RuntimeException =>
    } finally {
      closeableOnce.closed.get() shouldBe true
    }
  }

  "loanWithCloser" should "call close() only once" in {
    val countingCloseable = new CountingCloseable

    loanWithCloser(countingCloseable)(_.close()) to { resource =>
      /* do nothing */
    }

    countingCloseable.counter.get() shouldEqual 1
  }

}


class CloseableOnce extends AutoCloseable {

  val closed = new AtomicBoolean(false)

  override def close() = {
    closed.set(true)
  }

}

class CountingCloseable extends AutoCloseable {

  val counter = new AtomicInteger(0)

  override def close() = {
    counter.incrementAndGet()
    Unit
  }
}