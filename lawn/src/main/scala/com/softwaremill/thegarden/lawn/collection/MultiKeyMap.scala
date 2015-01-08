package com.softwaremill.thegarden.lawn.collection

import MultiKeyMap.Getter

/**
 * Simple multi-key map.
 *
 * @tparam T object to store
 */
class MultiKeyMap[T] private[collection](indexes: Map[Getter[T, Any], Map[Any, T]] =
                                         Map[Getter[T, Any], Map[Any, T]]()) {
  require(indexes.size > 0, "MultiKeyMap should have at least one index over data.")

  private lazy val getters = indexes.keys.toSeq

  def +(elem: T): MultiKeyMap[T] =
    new MultiKeyMap[T]((for (getter <- getters) yield {
      getter -> (indexes(getter) + (getter.get(elem) -> elem))
    }).toMap)

  def get[K](getter: Getter[T, Any], key: K): Option[T] = {
    for {index <- indexes.get(getter)
         value <- index.get(key)
    } yield value
  }

  def -(elem: T): MultiKeyMap[T] =
    new MultiKeyMap[T]((
      for (getter <- getters) yield {
        val valueMap = indexes(getter)
        getter -> (valueMap - getter.get(elem))
      }).toMap)

  def data: Seq[T] = indexes.head._2.values.toSeq

  def size = indexes.head._2.size

  def isEmpty = indexes.head._2.isEmpty

}

object MultiKeyMap {
  case class Getter[-S, +T](get: (S => T))
  def init[T, K <: Any](getters: Getter[T, Any]*): MultiKeyMap[T] =
    new MultiKeyMap(getters.map(_ -> Map[Any, T]()).toMap)
}