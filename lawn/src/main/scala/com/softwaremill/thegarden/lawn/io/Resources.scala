package com.softwaremill.thegarden.lawn.io

import java.util.zip.GZIPInputStream

import org.apache.commons.io.IOUtils

object Resources {

  val EOF = -1

  val DefaultBufferSize = 1024 * 4

  def inputStream(path: String) =
    this.getClass.getClassLoader.getResourceAsStream(path)

  def exists(path: String) =
    this.getClass.getClassLoader.getResource(path) != null

  def readToString(path: String) =
    IOUtils.toString(inputStream(path), "UTF-8")

  def toStringUngzipping(resourcePath: String) =
    IOUtils.toString(new GZIPInputStream(inputStream(resourcePath)))

  def toFile(path: String) =
    this.getClass.getClassLoader.getResource(path).getFile

}
