package com.softwaremill.thegarden.mongodbtest

import org.scalatest.{ShouldMatchers, FlatSpec}
import com.mongodb.casbah.Imports._
import com.mongodb.DBCollection


class FongoSupportSpec extends FlatSpec with ShouldMatchers {

  class TestClass(clearData : Boolean = false, afterAllBlock : (DBCollection) => Unit = _ => {}) extends FlatSpec with FongoSupport {

    override val clearDataBeforeEachTest: Boolean = clearData

    it should "run one test" in {
      import com.mongodb.casbah.Imports._

      val coll = fongo.getDB(dbName).getCollection("test_coll")
      coll.save(DBObject("foo" -> "bar"))
    }

    it should "run another test" in {
      val coll = fongo.getDB(dbName).getCollection("test_coll")
      coll.save(DBObject("foo2" -> "bar2"))
    }

    override protected def afterAll() = {
      afterAllBlock(fongo.getDB(dbName).getCollection("test_coll"))
      super.afterAll()
    }
  }

  it should "not clear data before every test if not asked to explicitly" in {
    var collectionCountAfterExecute: Long = -1

    new TestClass(false, {coll =>
      collectionCountAfterExecute = coll.count()
    }).execute

    collectionCountAfterExecute should equal(2)
  }

  it should "clear data before every test if that's explicitly set" in {
    var collectionCountAfterExecute: Long = -1

    new TestClass(true, {coll =>
      collectionCountAfterExecute = coll.count()
    }).execute

    collectionCountAfterExecute should equal(1)
  }

}
