package com.softwaremill.thegarden.web.scalatra

import scala.reflect.runtime._

trait ServletCompanion {
  val MappingPath : String
}

object ServletCompanions {
  def companionOf(servlet: AnyRef) = {
    // See http://stackoverflow.com/questions/11020746/get-companion-object-instance-with-new-scala-reflection-api
    val rootMirror = universe.runtimeMirror(servlet.getClass.getClassLoader)
    val moduleSymbol = rootMirror.moduleSymbol(servlet.getClass)
    val moduleMirror = rootMirror.reflectModule(moduleSymbol)
    moduleMirror.instance.asInstanceOf[ServletCompanion]
  }
}
