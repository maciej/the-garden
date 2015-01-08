package com.softwaremill.thegarden.lawn.collection

import com.softwaremill.thegarden.lawn.collection.MultiKeyMap.Getter
import org.scalatest.{Matchers, FlatSpec}

class MultiKeyMapSpec extends FlatSpec with Matchers {

  val StrLength = Getter((s: String) => s.length)
  val StrPrefix = Getter((s: String) => s.take(4))

  it should "fail to initialize without any index" in {
    a[RuntimeException] shouldBe thrownBy {
      MultiKeyMap.init()
    }
  }

  it should "get value indexed by string length" in {
    val m = MultiKeyMap.init(StrLength) + "foo"

    m.get(StrLength, 3) shouldEqual Some("foo")
  }

  ".get" should "return non if the key was not found" in {
    val m = MultiKeyMap.init(StrLength) + "foo"

    m.get(StrLength, 4) shouldEqual None
  }

  "- (minus)" should "remove a value from the map" in {
    val m = MultiKeyMap.init(StrLength) + "foo"

    (m - "foo").isEmpty shouldBe true
  }

  "- (minus)" should "not remove a nonexistent value from the map" in {
    val m = MultiKeyMap.init(StrLength) + "foo"

    (m - "bazz").isEmpty shouldBe false
  }

}
