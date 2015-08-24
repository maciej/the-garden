package me.maciejb.thegarden.metrics

import java.util.concurrent.atomic.AtomicInteger

import com.codahale.metrics.Gauge
import nl.grons.metrics.scala.MetricBuilder

import scala.concurrent.{ExecutionContext, Future}


class PushMetricHealthCheck private[PushMetricHealthCheck]() {

  import PushMetricHealthCheck._

  private[this] val status = new AtomicInteger(Statuses.Ok.id)
  private[PushMetricHealthCheck] val gauge = new Gauge[Int] {
    override def getValue = statusSnapshot
  }

  def setOk() = status.set(Statuses.Ok.id)
  def setError() = status.set(Statuses.Error.id)

  /* For testing */
  private[metrics] def statusSnapshot = status.get()
}

object PushMetricHealthCheck {
  object Statuses extends Enumeration {
    val Ok = Value(0, "ok")
    val Error = Value(1, "error")
  }

  def register(name: String, builder: MetricBuilder): PushMetricHealthCheck = {
    val healthCheck = new PushMetricHealthCheck()
    builder.registry.register(builder.baseName.append(name).name, healthCheck.gauge)
    healthCheck
  }

}

object FutureHealthCheck {

  implicit class FutureExt(fut: Future[_]) {
    def updateHealthCheck(healthCheck: PushMetricHealthCheck)(implicit ec: ExecutionContext) = {
      fut onSuccess { case _ => healthCheck.setOk() }
      fut onFailure { case _ => healthCheck.setError() }
    }
  }

}
