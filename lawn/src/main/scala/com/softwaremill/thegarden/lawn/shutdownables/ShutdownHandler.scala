package com.softwaremill.thegarden.lawn.shutdownables

import com.typesafe.scalalogging.slf4j.Logging
import scala.collection.mutable

import scala.language.implicitConversions

class ShutdownHandler extends Logging {

  private val shutdownableQueue = new mutable.SynchronizedQueue[Shutdownable]()

  def shutdown() {
    shutdownableQueue.dequeueAll(_ => true).reverse.foreach { service =>
      try {
        service.shutdown()
      } catch {
        case e: Exception =>
          val serviceClassName = service.getClass.getCanonicalName
          logger.error(s"Exception caught while closing $serviceClassName.", e)
      }
    }
  }

  private[shutdownables] def register(service: Shutdownable) {
    shutdownableQueue.enqueue(service)
  }

  private[shutdownables] def queueLength = shutdownableQueue.length
}

trait WithShutdownHandler {
  def shutdownHandler: ShutdownHandler

  implicit def anyToShutdownable[T](service: T) = new WrappedServiceToShutdown(service)

  implicit def wrapShutdownable[T <: Shutdownable](service: T) = new ServiceThatIsAlreadyShutdownable(service)

  final class ServiceThatIsAlreadyShutdownable[T <: Shutdownable](service: T) {
    def withShutdownHandled(): T = {
      new ShutdownableService(shutdownHandler, service).service
    }
  }

  final class WrappedServiceToShutdown[T](serviceToShutdown: T) {
    def onShutdown(closeFun: T => Unit): T = onShutdown(shutdownHandler)(closeFun)

    def onShutdown(customShutdownHandler: ShutdownHandler)(closeFun: T => Unit): T = {
      new WithShutdownBlock[T] {
        override protected val handler: ShutdownHandler = customShutdownHandler
        override protected val underlying: T = serviceToShutdown

        override def shutdown() = closeFun(underlying)
      }.service
    }
  }

}

trait DefaultShutdownHandlerModule extends WithShutdownHandler {

  lazy val shutdownHandler = new ShutdownHandler
}

trait ShutdownOnJVMTermination {
  this: WithShutdownHandler =>

  sys.addShutdownHook {
    shutdownHandler.shutdown()
  }
}
