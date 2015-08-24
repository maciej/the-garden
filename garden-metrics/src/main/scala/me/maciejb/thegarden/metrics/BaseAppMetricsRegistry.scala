package me.maciejb.thegarden.metrics

import java.lang.management.ManagementFactory

import com.codahale.metrics.{Metric, MetricSet, MetricRegistry}
import com.codahale.metrics.jvm.BufferPoolMetricSet
import com.codahale.metrics.jvm.GarbageCollectorMetricSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import nl.grons.metrics.scala.{MetricName, MetricBuilder}

import scala.collection.JavaConverters._

class BaseAppMetricsRegistry(val appPrefix: String) {

  val metricRegistry = {
    import MetricRegistryExtensions._
    val reg = new MetricRegistry()

    reg.registerAll(s"$appPrefix.jvm.gc", new GarbageCollectorMetricSet())
    reg.registerAll(s"$appPrefix.jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer))
    reg.registerAll(s"$appPrefix.jvm.memory", new MemoryUsageGaugeSet())
    reg.registerAll(s"$appPrefix.jvm.threads", new ThreadStatesGaugeSet())

    reg
  }
}

object MetricRegistryExtensions {
  implicit class MetricRegistryExt(reg: MetricRegistry) {
    def registerAll(prefix: String, metricSet: MetricSet): Unit = {
      metricSet.getMetrics.asScala.foreach {
        case (name, childSet: MetricSet) => registerAll(s"$prefix.$name", childSet)
        case (name, metric: Metric) => reg.register(s"$prefix.$name", metric)
      }
    }
  }
}

trait InstrumentedBuilder extends nl.grons.metrics.scala.InstrumentedBuilder {
  override protected lazy val metricBuilder =
    new MetricBuilder(MetricName(appMetricsRegistry.appPrefix), metricRegistry)

  protected def appMetricsRegistry: BaseAppMetricsRegistry

  override lazy val metricRegistry = appMetricsRegistry.metricRegistry
}
