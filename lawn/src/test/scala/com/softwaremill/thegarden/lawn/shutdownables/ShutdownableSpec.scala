package com.softwaremill.thegarden.lawn.shutdownables

import org.scalatest.{ShouldMatchers, FlatSpec}

class ShutdownableSpec extends FlatSpec with ShouldMatchers {

  behavior of "Module with ShutdownHandler"

  it should "not have CloseableService registered for closing if it was never referenced" in {
    val module = new ShutdownablesModule

    module.shutdownHandler.queueLength shouldEqual 0
  }

  it should "have CloseableService registered for closing if ServiceManager was referenced" in {
    val module = new ShutdownablesModule

    val serviceManager = module.serviceManager
    serviceManager.doNothing() /* Just to silence unused val warnings */

    module.shutdownHandler.queueLength shouldEqual 1
  }

}


class ShutdownablesModule {

  lazy val shutdownHandler = new QueueBackedShutdownHandler

  lazy val closeableService = new ShutdownableService(shutdownHandler, new CloseableService).service

  lazy val serviceManager = new ServiceManager(closeableService)
}
