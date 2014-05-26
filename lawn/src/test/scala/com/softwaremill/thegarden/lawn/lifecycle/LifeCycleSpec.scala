package com.softwaremill.thegarden.lawn.lifecycle

import org.scalatest.{ShouldMatchers, FlatSpec}

class LiceCycleSpec extends FlatSpec with ShouldMatchers {

  it should "not have CloseableService registered for closing if it was never referenced" in {
    val module = new LifeCycleModule

    module.lifecycleManager.queueLength shouldEqual 0
  }

  it should "have CloseableService registered for closing if ServiceManager was referenced" in {
    val module = new LifeCycleModule

    val serviceManager = module.serviceManager
    serviceManager.doNothing() /* Just to silence unused val warnings */

    module.lifecycleManager.queueLength shouldEqual 1
  }

}


class LifeCycleModule {

  lazy val lifecycleManager = new LifeCycleManager

  lazy val closeableService = new DestroyableLifeCycleService(lifecycleManager, new CloseableService).service

  lazy val serviceManager = new ServiceManager(closeableService)
}

