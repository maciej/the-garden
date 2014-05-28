package com.softwaremill.thegarden.lawn.shutdownables

/**
 * Trait describing a shutdownable service.
 *
 * If you're service holds to non-memory resources that need to be cleaned or at shutdown
 * or has some custom termination logic consider implementing this trait.
 */
trait Shutdownable {

  /**
   * Shutdowns the service.
   *
   * This method should block. It can optionally throw exceptions. The default ShutdownHandler will catch
   * and log all non-fatal exceptions.
   */
  def shutdown(): Unit
}
