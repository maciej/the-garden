package me.maciejb.thegarden.metrics

import java.util.concurrent.TimeUnit

import com.codahale.metrics.graphite.{Graphite, GraphiteReporter, GraphiteUDP}
import com.codahale.metrics.{Metric, MetricFilter, ScheduledReporter, Slf4jReporter}
import com.typesafe.scalalogging.{Logger, StrictLogging}
import nl.grons.metrics.scala.Implicits._
import org.slf4j.LoggerFactory

import scala.language.implicitConversions
import scala.util.control.NonFatal

class MetricReportingActivator(config: MetricsConfig, appMetricsRegistry: BaseAppMetricsRegistry,
                               metricsLogger: Logger = Logger(LoggerFactory.getLogger("metrics")))
  extends StrictLogging {

  private[this] var reporters: List[ScheduledReporter] = Nil
  private[this] var activated: Boolean = false

  def activate() = this.synchronized {
    if (!activated) {
      try {
        for (c <- config.logReporterOpt if c.enabled) {
          val filterJvmMetrics = { (name: String, _: Metric) => !name.contains("jvm.") }

          val builder = Slf4jReporter.forRegistry(appMetricsRegistry.metricRegistry)
            .outputTo(metricsLogger.underlying)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)

          if (!c.includeJvmStats) builder.filter(filterJvmMetrics)

          val logReporter = builder.build()

          logReporter.start(c.frequency.getSeconds, TimeUnit.SECONDS)
          reporters = logReporter :: reporters
        }

        for (c <- config.graphiteReporterOpt if c.enabled) {
          val sender = c.proto match {
            case GraphiteProtocols.Udp => new GraphiteUDP(c.host, c.port)
            case GraphiteProtocols.Tcp => new Graphite(c.host, c.port)
          }

          val graphiteReporter = GraphiteReporter.forRegistry(appMetricsRegistry.metricRegistry)
            .prefixedWith(c.prefix)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .build(sender)

          graphiteReporter.start(c.frequency.getSeconds, TimeUnit.SECONDS)

          reporters = graphiteReporter :: reporters
        }

      } finally {
        activated = true
      }
    }
  }


  def deactivate() = this.synchronized {
    if (activated) {
      try {
        for (reporter <- reporters) {
          try {
            reporter.stop()
          } catch {
            case NonFatal(e) => metricsLogger.warn(s"Caught an exception while deactivating reporter $reporter.", e)
          }
        }
      } finally {
        reporters = Nil
        activated = false
      }
    }
  }


}