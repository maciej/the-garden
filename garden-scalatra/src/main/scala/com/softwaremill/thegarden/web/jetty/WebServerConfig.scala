package com.softwaremill.thegarden.web.jetty

import com.softwaremill.thegarden.lawn.config.ConfigWithDefaults


trait WebServerConfig extends ConfigWithDefaults {

  lazy val webServerHost: String = getString("web.host", "0.0.0.0")
  lazy val webServerPort: Int = getInt("web.port", 8080)

}