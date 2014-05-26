package com.softwaremill.thegarden.lawn.lifecycle

import org.scalatest.{ShouldMatchers, FlatSpec}
import java.io.Closeable
import scala.collection.mutable.ArrayBuffer


class LifeCycleManagerSpec extends FlatSpec with ShouldMatchers {

  it should "close Closeables in reverse order" in {
    // Given
    val closeOrder = new ArrayBuffer[String]

    class CloseSecond extends Closeable {
      override def close() = closeOrder.append("second")
    }

    class CloseFirst extends Closeable {
      override def close() = closeOrder.append("first")
    }

    val manager = new LifeCycleManager
    manager.register(new CloseSecond)
    manager.register(new CloseFirst)

    // When
    manager.close()

    // Then
    closeOrder.toSeq shouldEqual Seq("first", "second")
  }
}