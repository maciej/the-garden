package com.softwaremill.thegarden.spray.testkit

import akka.actor.{ActorSystem, ActorRef}
import akka.io.IO
import akka.testkit.{TestKit, TestProbe}
import spray.can.Http


trait SprayServerSupport {

  this: TestKit =>

  protected val httpPort: Int

  protected def newServer(service: ActorRef) = SprayServerSupport.newServer(service, httpPort)

  protected def unbind(listener: ActorRef): Unit = SprayServerSupport.unbind(listener)

  protected def withServer(service: ActorRef)(testCode: => Unit) = {
    val listener = newServer(service)
    try {
      testCode
    } finally {
      unbind(listener)
    }
  }

}

object SprayServerSupport {

  def newServer(service: ActorRef, httpPort: Int)(implicit system: ActorSystem): ActorRef = {
    val commander = TestProbe()
    commander.send(IO(Http), Http.Bind(service, "localhost", httpPort))
    commander.expectMsgType[Http.Bound]
    commander.sender()
  }

  def unbind(listener: ActorRef)(implicit system: ActorSystem): Unit = {
    val probe = TestProbe()
    probe.send(listener, Http.Unbind)
    probe.expectMsg(Http.Unbound)
  }

}