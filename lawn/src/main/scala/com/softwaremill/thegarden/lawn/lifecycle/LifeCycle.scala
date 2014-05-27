package com.softwaremill.thegarden.lawn.lifecycle

trait WithLifeCycle[T] extends Closeable {

  protected val manager: LifeCycleManager

  protected def registerCloseable(closeable: Closeable) = manager.register(closeable)

  def close()

  protected val underlying: T

  lazy val service: T = {
    registerCloseable(this)
    underlying
  }

}

class DestroyableLifeCycleService[T <: Closeable](protected val manager: LifeCycleManager, protected val underlying: T)
  extends WithLifeCycle[T] {

  override def close() = underlying.close()
}
