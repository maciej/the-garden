package com.softwaremill.thegarden.mongodbtest

import com.github.fakemongo.Fongo
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import org.scalatest.{BeforeAndAfterEach, Suite, BeforeAndAfterAll}

trait FongoSupport extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  private def newUuid() = java.util.UUID.randomUUID.toString

  protected val dbName = newUuid()

  lazy val fongo = new Fongo(newUuid())

  val clearDataBeforeEachTest = false

  override protected def beforeAll() = {
    super.beforeAll()

    val mongo = fongo.getMongo
    MongoDB.defineDb(DefaultMongoIdentifier, mongo, dbName)
  }

  override protected def beforeEach() = {
    if (clearDataBeforeEachTest)
      fongo.dropDatabase(dbName)
  }

  override protected def afterAll() = {
    MongoDB.close
    super.afterAll()
  }

}
