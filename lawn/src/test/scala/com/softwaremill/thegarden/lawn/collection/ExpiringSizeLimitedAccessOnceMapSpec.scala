package com.softwaremill.thegarden.lawn.collection

import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.ExecutionContext.Implicits._

class ExpiringSizeLimitedAccessOnceMapSpec extends FlatSpec with Matchers {

  it should "contain a value put into it" in {
    val map = AccessOnceMap.expiringSizeLimited[String, Int]()

    map.put("key", 42)

    map.obtain("key").getOrElse("Map did not contain requested entry.") shouldEqual 42
  }

  it should "remove a value once obtained" in {
    val map = AccessOnceMap.expiringSizeLimited[String, Int]()

    map.put("key", 42)
    map.obtain("key")
    map.obtain("key") shouldBe None
  }

}