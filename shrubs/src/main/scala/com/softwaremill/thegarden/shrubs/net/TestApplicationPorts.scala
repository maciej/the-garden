package com.softwaremill.thegarden.shrubs.net

import java.io.IOException
import java.net.ServerSocket


/**
 * Thread-safe provider of a sequence of integers safe to use as port numbers.
 */
object TestApplicationPorts {

  @volatile
  private var portNumber = 1024

  val BlacklistedPorts = Set(3000, 4000, 8443, 8080)

  private def isAvailable(port: Int): Boolean = {
    var socket: ServerSocket = null
    try {
      socket = new ServerSocket(port)
      true
    } catch {
      case _: IOException => false
    } finally {
      if (socket != null)
        try {
          socket.close()
        } catch {
          case _: IOException => ()
        }
    }
  }

  def next(): Int = {
    synchronized {
      portNumber += 1

      if (portNumber >= math.pow(2, 16).toInt - 1)
        throw new RuntimeException("Whoops! Too many tests? To many applications started?")

      while (BlacklistedPorts(portNumber) || !isAvailable(portNumber)) {
        portNumber += 1
      }

      portNumber
    }
  }
}
