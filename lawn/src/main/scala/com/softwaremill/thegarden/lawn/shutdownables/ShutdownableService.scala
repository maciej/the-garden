package com.softwaremill.thegarden.lawn.shutdownables

private[shutdownables] trait WithShutdownBlock[T] extends Shutdownable {

  protected val handler: ShutdownHandler

  protected def registerShutdownable(shutdownable: Shutdownable) = handler.register(shutdownable)

  def shutdown()

  protected val underlying: T

  lazy val service: T = {
    registerShutdownable(this)
    underlying
  }

}

private[shutdownables] class ShutdownableService[T <: Shutdownable](protected val handler: ShutdownHandler,
                                                                    protected val underlying: T)
  extends WithShutdownBlock[T] {

  override def shutdown() = underlying.shutdown()
}
