/*
 * Copyright 2011-2012 Typesafe, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.softwaremill.thegarden.lawn.config

import java.time.Duration

import java.util.Properties

import com.typesafe.config.{ConfigFactory, Config}

import scala.language.implicitConversions


/**
 * Extension methods to make Typesafe Config easier to use.
 * Inspired by the same class in Slick.
 * Differences between Slick's version include:
 * - using [[java.time.Duration]] instead of [[scala.concurrent.duration.Duration]] to match typesafe-config 1.3.0 API.
 */
class ConfigExtensionMethods(val c: Config) extends AnyVal {

  import scala.collection.JavaConverters._

  def getBooleanOr(path: String, default: => Boolean = false) = if (c.hasPath(path)) c.getBoolean(path) else default
  def getIntOr(path: String, default: => Int = 0) = if (c.hasPath(path)) c.getInt(path) else default
  def getLongOr(path: String, default: => Long = 0L) = if (c.hasPath(path)) c.getLong(path) else default
  def getStringOr(path: String, default: => String = null) = if (c.hasPath(path)) c.getString(path) else default
  def getConfigOr(path: String, default: => Config = ConfigFactory.empty()) =
    if (c.hasPath(path)) c.getConfig(path) else default

  def getDurationOr(path: String, default: => Duration = Duration.ZERO) =
    if (c.hasPath(path)) c.getDuration(path) else default

  def getPropertiesOr(path: String, default: => Properties = null) =
    if (!c.hasPath(path)) default
    else {
      val props = new Properties(null)
      c.getObject(path).asScala.foreach { case (k, v) => props.put(k, v.unwrapped.toString) }
      props
    }

  def getBooleanOpt(path: String): Option[Boolean] = if (c.hasPath(path)) Some(c.getBoolean(path)) else None
  def getIntOpt(path: String): Option[Int] = if (c.hasPath(path)) Some(c.getInt(path)) else None
  def getLongOpt(path: String): Option[Long] = if (c.hasPath(path)) Some(c.getLong(path)) else None
  def getStringOpt(path: String) = Option(getStringOr(path))
  def getPropertiesOpt(path: String) = Option(getPropertiesOr(path))
  def getDurationOpt(path: String) = if (c.hasPath(path)) Some(getDurationOr(path)) else None
  def getConfigOpt(path: String) = if (c.hasPath(path)) Some(c.getConfig(path)) else None

}

object ConfigExtensionMethods {
  @inline implicit def configExtensionMethods(c: Config): ConfigExtensionMethods = new ConfigExtensionMethods(c)
}
