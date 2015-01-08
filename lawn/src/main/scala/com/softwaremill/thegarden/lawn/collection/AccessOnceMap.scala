package com.softwaremill.thegarden.lawn.collection

import java.util.concurrent.{Executors, TimeUnit}

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import com.softwaremill.thegarden.lawn.concurrent.ThreadFactories
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

import scala.ref.WeakReference

trait AccessOnceMap[K, V] {

  def obtain(k: K): Option[V]

  def put(k: K, v: V): Unit

}

object AccessOnceMap {

  def expiringSizeLimited[K, V](initialCapacity: Int = 16, maxCapacity: Int = 1024, expireAfter: FiniteDuration = 1.hour)
                               (implicit ec: ExecutionContext): AccessOnceMap[K, V] =
    new ExpiringSizeLimitedAccessOnceMap[K, V](initialCapacity, maxCapacity, expireAfter)

}

class ExpiringSizeLimitedAccessOnceMap[K, V](initialCapacity: Int,
                                             maxCapacity: Int,
                                             expireAfter: FiniteDuration)
                                            (implicit ec: ExecutionContext) extends AccessOnceMap[K, V] {

  import ExpiringSizeLimitedAccessOnceMap._

  private val store = new ConcurrentLinkedHashMap.Builder[K, Entry[V]]
    .initialCapacity(initialCapacity)
    .maximumWeightedCapacity(maxCapacity)
    .build()

  private val cleanerDelay = expireAfter / 2

  cleaner.schedule(new CleanerOp[K, V](WeakReference(this), cleanerDelay), cleanerDelay.toMillis, TimeUnit.MILLISECONDS)

  override def obtain(k: K): Option[V] = for (e <- Option(store.remove(k))) yield e.v

  override def put(k: K, v: V) = store.put(k, Entry(v, DateTime.now()))

  private[collection] def cleanExpired() = {
    import scala.collection.JavaConverters._
    val entrySet = store.entrySet().asScala
    for (e <- entrySet) {
      val (k, v) = (e.getKey, e.getValue)
      if (v.isExpired(expireAfter)) store.remove(k)
    }
  }
}

private[collection] class CleanerOp[K, V](mapRef: WeakReference[ExpiringSizeLimitedAccessOnceMap[K, V]],
                                                                cleanerDelay: FiniteDuration)
                                                               (implicit ec: ExecutionContext) extends Runnable {
  override def run() = {
    mapRef.get.foreach { map =>
      Future {map.cleanExpired()}
      // Next cleaning is scheduled only as long as the map is not garbage collected
      ExpiringSizeLimitedAccessOnceMap.cleaner.schedule(this, cleanerDelay.toMillis, TimeUnit.MILLISECONDS)
    }
  }

}

object ExpiringSizeLimitedAccessOnceMap {

  private[collection] lazy val cleaner = Executors.newSingleThreadScheduledExecutor(ThreadFactories.daemonThreadFactory)

  case class Entry[V](v: V, createdAt: DateTime) {
    def isExpired(expireAfter: FiniteDuration) =
      createdAt.plusMillis(expireAfter.toMillis.toInt).isAfter(DateTime.now())
  }

}
