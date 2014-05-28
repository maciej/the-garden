package com.softwaremill.thegarden.lawn.shutdownables

import com.typesafe.scalalogging.slf4j.Logging

class CloseableService extends Shutdownable with Logging {

  def shutdown() = {
    logger.debug("Closing CloseableService.")
  }
}

class ServiceManager(closeableService: CloseableService) {

  def doNothing() = {}
}
