package me.maciejb.thegarden.metrics

import com.codahale.metrics.{MetricFilter, Metric}

import scala.language.implicitConversions

// Remove if https://github.com/erikvanoosten/metrics-scala/pull/62 merged and released
object MetricImplicits {
  implicit def funToMetricFilter(filterFun: (String, Metric) => Boolean): MetricFilter = new MetricFilter {
    override def matches(name: String, metric: Metric) = filterFun(name, metric)
  }
}
