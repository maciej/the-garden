package com.softwaremill.thegarden.lawn.lifecycle

import org.scalatest.{ShouldMatchers, FlatSpec}

class DefaultLifeCycleManagerTest extends FlatSpec with ShouldMatchers {

  class UsingDLCModule extends DefaultLifeCycleManagerModule {

    lazy val withLifecycleService = withLifeCycle(new CloseableService) {
      service => service.close()
    }

    lazy val withCloseableService = withCloseable(new CloseableService)

    lazy val nicelyCloseableServiceManager = new ServiceManager(withCloseableService)

    lazy val genericCloseableServiceManager = new ServiceManager(withLifecycleService)

  }

  it should "not have any CloseableServices registered for closing if not referenced" in {
    val module = new UsingDLCModule

    module.lifecycleManager.queueLength shouldEqual 0
  }

  it should "have both closeable services registered if referenced" in {
    val module = new UsingDLCModule

    Seq(module.nicelyCloseableServiceManager, module.genericCloseableServiceManager)

    module.lifecycleManager.queueLength shouldEqual 2
  }

}
