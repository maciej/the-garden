package com.softwaremill.thegarden.lawn.lifecycle

import com.typesafe.scalalogging.slf4j.Logging
import scala.collection.mutable
import java.io.Closeable

class LifeCycleManager extends Logging {

  private val closeableQueue = new mutable.SynchronizedQueue[Closeable]()

  def close() {
    closeableQueue.dequeueAll(_ => true).reverse.foreach { service =>
      try {
        service.close()
      } catch {
        case e: Exception =>
          val serviceClassName = service.getClass.getCanonicalName
          logger.error(s"Exception caught while closing $serviceClassName.", e)
      }
    }
  }

  private[lifecycle] def register(service : Closeable) {
    closeableQueue.enqueue(service)
  }

  private[lifecycle] def queueLength = closeableQueue.length
}

trait WithLifeCycleManager {
  def lifecycleManager: LifeCycleManager
}

trait DefaultLifeCycleManagerModule extends WithLifeCycleManager {

  lazy val lifecycleManager = new LifeCycleManager

  def withLifeCycle[T](lifecycleService: T)(closeFun: (T => Unit)): T = {
    new WithLifeCycle[T] {
      override protected val manager: LifeCycleManager = lifecycleManager
      override protected val underlying: T = lifecycleService

      override def close() = closeFun(underlying)
    }.service
  }

  def withCloseable[T <: Closeable](lifecycleService: T) =
    new DestroyableLifeCycleService[T](lifecycleManager, lifecycleService).service
}

trait DestroyOnShutdown {
  this: WithLifeCycleManager =>

  sys.addShutdownHook {
    lifecycleManager.close()
  }
}
