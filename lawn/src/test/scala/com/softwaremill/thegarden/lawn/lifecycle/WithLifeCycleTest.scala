package com.softwaremill.thegarden.lawn.lifecycle

import org.scalatest.{ShouldMatchers, FlatSpec}
import com.typesafe.scalalogging.slf4j.Logging

class WithLifeCycleTest extends FlatSpec with ShouldMatchers {

  it should "not have CloseableService registered for closing if it was never referenced" in {
    val module = new LifeCycleModule

    module.lifecycleManager.closeableQueue.length shouldEqual 0
  }

  it should "have CloseableService registered for closing if ServiceManager was referenced" in {
    val module = new LifeCycleModule

    val serviceManager = module.serviceManager
    serviceManager.doNothing() /* Just to silence unused val warnings */

    module.lifecycleManager.closeableQueue.length shouldEqual 1
  }
}


class CloseableService extends Closeable with Logging {

  def close() = {
    logger.debug("Closing CloseableService.")
  }
}

class ServiceManager(closeableService: CloseableService) {

  def doNothing() = {}
}

class LifeCycleModule {

  lazy val lifecycleManager = new LifeCycleManager

  lazy val closeableService = new CloseableLifeCycleService(lifecycleManager, new CloseableService).service

  lazy val serviceManager = new ServiceManager(closeableService)
}