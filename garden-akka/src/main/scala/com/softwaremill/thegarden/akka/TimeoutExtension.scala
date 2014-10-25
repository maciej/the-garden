package com.softwaremill.thegarden.akka

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration


object TimeoutExtension {

  // Taken from http://semberal.github.io/scala-future-timeout-patterns.html
  def withTimeout[T](timeout: FiniteDuration)(future: Future[T])(implicit actorSystem: ActorSystem,
                                                                 ec: ExecutionContext): Future[T] = {

    val timeoutFuture = akka.pattern.after(timeout, using = actorSystem.scheduler) {
      Future.failed(new TimeoutException(s"The future did not complete in $timeout."))
    }

    Future.firstCompletedOf(future :: timeoutFuture :: Nil)
  }

}
