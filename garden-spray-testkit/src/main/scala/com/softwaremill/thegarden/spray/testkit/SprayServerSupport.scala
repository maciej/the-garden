package com.softwaremill.thegarden.spray.testkit

import akka.actor.ActorRef
import akka.io.IO
import akka.testkit.{TestKit, TestProbe}
import spray.can.Http


trait SprayServerSupport {

  this: TestKit =>

  protected val httPort: Int

  protected def newServer(service: ActorRef) = {
    val commander = TestProbe()
    commander.send(IO(Http), Http.Bind(service, "localhost", httPort))
    commander.expectMsgType[Http.Bound]
    commander.sender()
  }

  protected def unbind(listener: ActorRef): Unit = {
    val probe = TestProbe()
    probe.send(listener, Http.Unbind)
    probe.expectMsg(Http.Unbound)
  }

  protected def withServer(service: ActorRef)(testCode: => Unit) = {
    val listener = newServer(service)
    try {
      testCode
    } finally {
      unbind(listener)
    }
  }

}
