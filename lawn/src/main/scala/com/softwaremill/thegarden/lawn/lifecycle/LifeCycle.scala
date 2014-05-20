package com.softwaremill.thegarden.lawn.lifecycle

import scala.collection.mutable
import com.typesafe.scalalogging.slf4j.Logging
import scala.ref.WeakReference


class LifeCycleManager extends Logging {
  val closeableQueue = new mutable.SynchronizedQueue[WeakReference[Closeable]]()

  def close() {
    closeableQueue.dequeueAll(_ => true).foreach { serviceRef =>
      serviceRef.get.map { service =>
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
}

trait WithLifeCycle[T] extends Closeable {

  protected val manager: LifeCycleManager

  protected def registerCloseable(closeable: Closeable) = {
    manager.closeableQueue.enqueue(WeakReference(closeable))
  }

  def close()

  protected val underlying: T

  val service: T = {
    registerCloseable(this)
    service
  }

}

class CloseableLifeCycleService[T <: Closeable](protected val manager: LifeCycleManager, protected val underlying: T)
  extends WithLifeCycle[T] {

  override def close() = underlying.close()
}



trait Closeable {
  def close()
}
