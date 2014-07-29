package com.softwaremill.thegarden.spray.directives

import java.io.File

import spray.routing._

private[directives] trait JarOrFileResolver {
  def getWebappFile(path: String): Route

  def getWebappDirectory(path: String): Route
}

private[directives] trait ForwardingJarOrFileResolver extends JarOrFileResolver {
  protected val underlyingJarOrFileResolver: JarOrFileResolver

  override def getWebappDirectory(path: String) = underlyingJarOrFileResolver.getWebappDirectory(path)

  override def getWebappFile(path: String) = underlyingJarOrFileResolver.getWebappFile(path)
}

trait WebappPathDirectives extends ForwardingJarOrFileResolver with HttpService {

  protected val cacheFiles: Boolean

  private def runningFromJar_? = {
    try {
      val cs = getClass.getProtectionDomain.getCodeSource
      cs.getLocation.toURI.getPath.endsWith(".jar")
    } catch {
      case e: Exception => true
    }
  }

  override protected val underlyingJarOrFileResolver: JarOrFileResolver = {
    if (runningFromJar_?)
      ResourceResolver
    else
      FileResolver
  }

  object ResourceResolver extends JarOrFileResolver {
    private val ResourcePathPrefix = "webapp"

    override def getWebappFile(path: String) = getFromResource(ResourcePathPrefix + "/" + path)

    override def getWebappDirectory(path: String) = getFromResourceDirectory(ResourcePathPrefix + "/" + path)
  }

  object FileResolver extends JarOrFileResolver {
    private val FilePathPrefix = "web/webapp"

    implicit val routingSettings = RoutingSettings.default.copy(fileGetConditional = cacheFiles)

    override def getWebappFile(path: String) = {
      val fullPath = FilePathPrefix + File.separator + path
      getFromFile(fullPath)
    }

    override def getWebappDirectory(path: String) = {
      val fullPath = FilePathPrefix + File.separator + path
      getFromDirectory(fullPath)
    }

  }

}
