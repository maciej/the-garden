package com.softwaremill.thegarden.shrubs.concurrent

import java.util.concurrent.ExecutionException

import org.scalatest.Assertions
import org.scalatest.concurrent.AsyncAssertions

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait FutureFailures {

  implicit class Failing[A](val f: Future[A]) extends Assertions with AsyncAssertions {

    def failing[T <: Throwable : Manifest](implicit ec: ExecutionContext) = {
      val w = new Waiter
      f onComplete {
        case Failure(e) => w(throw e); w.dismiss()
        case Success(_) => w.dismiss()
      }
      intercept[T] {
        w.await
      }
    }

    def failureMatching[T <: Throwable : Manifest](exceptionPredicate: Throwable => Boolean)
                                                  (implicit ec: ExecutionContext) = {
      val w = new Waiter
      def processException(e: Throwable) = {
        if (!exceptionPredicate(e)) {
          w(fail("Unexpected exception.", e))
        }
        w.dismiss()
      }

      f onComplete {
        case Failure(e: ExecutionException) => processException(e.getCause)
        case Failure(e) => processException(e)
        case Success(_) =>
          w(fail("Future should not succeed."))
          w.dismiss()
      }
      w.await

    }
  }
}
