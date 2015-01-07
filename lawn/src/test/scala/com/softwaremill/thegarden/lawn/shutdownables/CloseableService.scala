package com.softwaremill.thegarden.lawn.shutdownables

import com.typesafe.scalalogging.LazyLogging

class CloseableService extends Shutdownable with LazyLogging {

  def shutdown() = {
    logger.debug("Closing CloseableService.")
  }
}

class ServiceManager(closeableService: CloseableService) {

  def doNothing() = {}
}
