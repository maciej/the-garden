package com.softwaremill.thegarden.lawn.config


trait HttpServerConfig extends ConfigWithDefaults {

  lazy val httpHost = getString("http-server.host", "0.0.0.0")
  lazy val httpPort = getInt("http-server.port", 8080)

}
