package com.softwaremill.thegarden.web.jetty

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import java.net.InetSocketAddress

trait EmbeddedJetty {
  protected var jetty: Server = null

  def startJetty() {
    jetty = new Server(jettyAddress)
    jetty.setHandler(prepareContext())
    jetty.start()
  }

  protected def prepareContext() = {
    val context = new WebAppContext()
    context.setContextPath("/")
    setResourceBase(context)
    context
  }

  def stopJetty() {
    jetty.stop()
  }

  protected def setResourceBase(context: WebAppContext)

  def webServerConfig: WebServerConfig

  lazy val jettyAddress = new InetSocketAddress(webServerConfig.webServerHost, webServerConfig.webServerPort)
}
