package com.softwaremill.thegarden.web.jetty

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import java.net.InetSocketAddress

class EmbeddedJetty(config: WebServerConfig, contextAttributes: Map[String, AnyRef] = Map()) {

  lazy val jetty = {
    val server = new Server(new InetSocketAddress(config.webServerHost, config.webServerPort))
    server.setHandler(prepareContext())
    server
  }

  def startJetty() {
    jetty.start()
  }

  protected def prepareContext() = {
    val webContext = new WebAppContext()
    webContext.setContextPath("/")
    val webappDirInsideJar = webContext.getClass.getClassLoader.getResource("webapp").toExternalForm
    webContext.setWar(webappDirInsideJar)

    contextAttributes.foreach {
      case (k, v) =>
        webContext.setAttribute(k, v)
    }

    webContext
  }

  def stopJetty() {
    jetty.stop()
  }

}
