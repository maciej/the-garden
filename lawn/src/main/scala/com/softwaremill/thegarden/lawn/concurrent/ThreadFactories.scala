package com.softwaremill.thegarden.lawn.concurrent

import java.util.concurrent.ThreadFactory

object ThreadFactories {
  def daemonThreadFactory = new DaemonThreadFactory(new PlainThreadFactory)
  def daemonThreadFactory(underlying : ThreadFactory) = new DaemonThreadFactory(underlying)
}

class DaemonThreadFactory(val underlying: ThreadFactory) extends ThreadFactory {
  override def newThread(r: Runnable) = {
    val thread = underlying.newThread(r)
    thread.setDaemon(true)
    thread
  }
}

class PlainThreadFactory extends ThreadFactory {
  override def newThread(r: Runnable) = new Thread(r)
}
