package com.softwaremill.thegarden.lawn.lifecycle

import scala.collection.mutable
import com.typesafe.scalalogging.slf4j.Logging
import scala.ref.WeakReference


class LifeCycleManager extends Logging {

  /*
   * TODO Closeables should be weakly referenced by their T
   */

  val closeableQueue = new mutable.SynchronizedQueue[Closeable]()

  def close() {
    closeableQueue.dequeueAll(_ => true).foreach { service =>
        try {
          service.close()
        } catch {
          case e: Exception =>
            val serviceClassName = service.getClass.getCanonicalName
            logger.error(s"Exception caught while closing $serviceClassName.", e)
        }
      }
  }
}

trait DefaultLifeCycleManagerModule {

  lazy val lifecycleManager = new LifeCycleManager

  def withLifeCycle[T](lifecycleService: T)(closeFun: (T => Unit)): T = {
    new WithLifeCycle[T] {
      override protected val manager: LifeCycleManager = lifecycleManager
      override protected val underlying: T = lifecycleService

      override def close() = closeFun(underlying)
    }.service
  }

  def withCloseable[T <: Closeable](lifecycleService: T) =
    new CloseableLifeCycleService[T](lifecycleManager, lifecycleService).service
}

trait WithLifeCycle[T] extends Closeable {

  protected val manager: LifeCycleManager

  protected def registerCloseable(closeable: Closeable) = {
    manager.closeableQueue.enqueue(closeable)
  }

  def close()

  protected val underlying: T

  lazy val service: T = {
    registerCloseable(this)
    underlying
  }

}

class CloseableLifeCycleService[T <: Closeable](protected val manager: LifeCycleManager, protected val underlying: T)
  extends WithLifeCycle[T] {

  override def close() = underlying.close()
}


trait Closeable {
  def close()
}
