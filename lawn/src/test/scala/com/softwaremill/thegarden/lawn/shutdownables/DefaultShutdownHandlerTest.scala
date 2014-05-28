package com.softwaremill.thegarden.lawn.shutdownables

import org.scalatest.{ShouldMatchers, FlatSpec}

class DefaultShutdownHandlerTest extends FlatSpec with ShouldMatchers {

  class UsingShutdownableSyntaxCLCModule extends DefaultShutdownHandlerModule {
    lazy val shutdownableService = new CloseableService onShutdown {
      _.shutdown()
    }
  }

  it should "not have any CloseableServices registered for closing if not referenced" in {
    val module = new UsingShutdownableSyntaxCLCModule

    module.shutdownHandler.queueLength shouldEqual 0
  }

  it should "register a service once referenced" in {
    val module = new UsingShutdownableSyntaxCLCModule

    module.shutdownableService

    module.shutdownHandler.queueLength shouldEqual 1
  }

}
