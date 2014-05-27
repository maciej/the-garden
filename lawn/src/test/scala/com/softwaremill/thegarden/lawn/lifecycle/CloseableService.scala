package com.softwaremill.thegarden.lawn.lifecycle

import com.typesafe.scalalogging.slf4j.Logging

class CloseableService extends Closeable with Logging {

  def close() = {
    logger.debug("Closing CloseableService.")
  }
}

class ServiceManager(closeableService: CloseableService) {

  def doNothing() = {}
}