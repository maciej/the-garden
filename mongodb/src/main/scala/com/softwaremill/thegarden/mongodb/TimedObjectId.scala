package com.softwaremill.thegarden.mongodb

import org.bson.types.ObjectId
import org.joda.time.DateTime


object TimedObjectId {
  def withTrailingZeroes(dt: DateTime): ObjectId = {
    val unixTimeStr = (dt.getMillis / 1000).toHexString
    val zeros = "0" * 16
    new ObjectId(unixTimeStr + zeros)
  }

  def withDefaults(dt: DateTime): ObjectId = {
    val unixTimeStr = (dt.getMillis / 1000).toHexString
    val trailing = new ObjectId().toHexString.takeRight(16)
    new ObjectId(unixTimeStr + trailing)
  }
}
