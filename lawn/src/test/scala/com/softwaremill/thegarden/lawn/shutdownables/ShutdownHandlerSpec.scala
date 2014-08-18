package com.softwaremill.thegarden.lawn.shutdownables

import org.scalatest.{ShouldMatchers, FlatSpec}
import scala.collection.mutable.ArrayBuffer


class ShutdownHandlerSpec extends FlatSpec with ShouldMatchers {

  behavior of "ShutdownHandler"

  it should "shutdown Shutdownables in reverse order" in {
    // Given
    val shutdownOrder = new ArrayBuffer[String]

    class ShutdownSecond extends Shutdownable {
      override def shutdown() = shutdownOrder.append("second")
    }

    class ShutdownFirst extends Shutdownable {
      override def shutdown() = shutdownOrder.append("first")
    }

    val manager = new ShutdownHandler
    manager.register(new ShutdownSecond)
    manager.register(new ShutdownFirst)

    // When
    manager.shutdown()

    // Then
    shutdownOrder.toSeq shouldEqual Seq("first", "second")
  }
}
