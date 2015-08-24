package me.maciejb.thegarden.metrics

import com.codahale.metrics.MetricRegistry
import me.maciejb.thegarden.metrics.PushMetricHealthCheck.Statuses
import nl.grons.metrics.scala.{MetricBuilder, MetricName}
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future


class FutureHealthCheckSpec extends FlatSpec with Matchers with Eventually {

  import FutureHealthCheck._

  import scala.concurrent.ExecutionContext.Implicits._

  def newHealthCheck(name: String)(t: (PushMetricHealthCheck) => Unit) = {
    val registry = new MetricRegistry()
    val builder = new MetricBuilder(MetricName(""), registry)
    val healthCheck = PushMetricHealthCheck.register(name, builder)
    t(healthCheck)
  }

  for ((fut, name, status) <- Seq(
    (Future.failed(new RuntimeException()), "fails", Statuses.Error),
    (Future.successful(1), "successful", Statuses.Ok)
  )) {
    it should s"set the health-check status to ${status.toString} when the backing future is $name" in {
      newHealthCheck(name) { healthCheck =>
        fut.updateHealthCheck(healthCheck)
        eventually {
          healthCheck.statusSnapshot shouldEqual status.id
        }
      }
    }
  }

}
