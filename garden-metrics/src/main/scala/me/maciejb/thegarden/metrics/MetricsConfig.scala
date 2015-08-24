package me.maciejb.thegarden.metrics

import java.time.Duration

import com.typesafe.config.{ConfigFactory, Config}
import com.softwaremill.thegarden.lawn.config.ConfigExtensionMethods._
import me.maciejb.thegarden.metrics.GraphiteProtocols.GraphiteProtocol

trait ReporterConfig {
  def enabled: Boolean
  def frequency: Duration
}

case class LogReporterConfig(enabled: Boolean, frequency: Duration,
                             includeJvmStats: Boolean = false) extends ReporterConfig
case class GraphiteReporterConfig(enabled: Boolean, frequency: Duration,
                                  prefix: String, host: String, port: Int,
                                  proto: GraphiteProtocol)
  extends ReporterConfig

object GraphiteProtocols extends Enumeration {
  type GraphiteProtocol = Value

  val Tcp = Value("tcp")
  val Udp = Value("udp")

  val DefaultProtocol = Tcp

  private[this] val valuesMap: Map[String, GraphiteProtocol] =
    values.map { v => v.toString -> v }.toMap

  def safeWithName(name: String) = {
    val normalizedName = name.toLowerCase.trim
    valuesMap.getOrElse(normalizedName, DefaultProtocol)
  }
}

case class MetricsConfig(logReporterOpt: Option[LogReporterConfig], graphiteReporterOpt: Option[GraphiteReporterConfig])

object MetricsConfig {
  val ConfigPrefix = "metrics"

  def build(config: Config = ConfigFactory.load().getConfig(ConfigPrefix)) = {
    val graphiteReporterOpt = for (c <- config.getConfigOpt("graphite_reporter")) yield {
      GraphiteReporterConfig(
        enabled = c.getBooleanOr("enabled", default = false),
        frequency = c.getDurationOr("frequency", Duration.ofSeconds(60)),
        prefix = c.getString("prefix"),
        host = c.getString("host"),
        port = c.getInt("port"),
        proto = GraphiteProtocols.safeWithName(c.getStringOr("proto",
          default = GraphiteProtocols.DefaultProtocol.toString))
      )
    }
    val logReporterOpt = for (c <- config.getConfigOpt("log_reporter")) yield {
      LogReporterConfig(
        enabled = c.getBooleanOr("enabled", default = false),
        frequency = c.getDurationOr("frequency", Duration.ofSeconds(60))
      )
    }

    MetricsConfig(logReporterOpt, graphiteReporterOpt)
  }
}
