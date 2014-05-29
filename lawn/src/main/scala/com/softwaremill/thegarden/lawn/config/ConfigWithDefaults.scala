package com.softwaremill.thegarden.lawn.config

import com.typesafe.config.Config


trait ConfigWithDefaults {

  def rootConfig: Config

  def getBoolean(path: String, default: Boolean) = ifHasPath(path, default) {
    _.getBoolean(path)
  }

  def getString(path: String, default: String) = ifHasPath(path, default) {
    _.getString(path)
  }

  def getInt(path: String, default: Int) = ifHasPath(path, default) {
    _.getInt(path)
  }

  def getConfig(path: String, default: Config) = ifHasPath(path, default) {
    _.getConfig(path)
  }

  def getOptionalString(path: String, default: Option[String] = None) = getOptional(path) {
    _.getString(path)
  }

  private def ifHasPath[T](path: String, default: T)(get: Config => T): T = {
    if (rootConfig.hasPath(path)) get(rootConfig) else default
  }

  private def getOptional[T](fullPath: String, default: Option[T] = None)(get: Config => T) = {
    if (rootConfig.hasPath(fullPath)) {
      Some(get(rootConfig))
    } else {
      default
    }
  }


}
