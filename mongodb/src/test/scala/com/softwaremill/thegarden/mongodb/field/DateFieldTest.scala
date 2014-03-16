package com.softwaremill.thegarden.mongodb.field

import org.scalatest.{ShouldMatchers, FlatSpec}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import com.github.fakemongo.Fongo
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import org.joda.time.DateTime

class DateFieldRecord extends MongoRecord[DateFieldRecord] with ObjectIdPk[DateFieldRecord] {
  override def meta = DateFieldRecord

  object createdAt extends DateField(this) with UnderscoreName
}

object DateFieldRecord extends DateFieldRecord with MongoMetaRecord[DateFieldRecord] {
  override val collectionName = "date_field_docs"
}

class DateFieldTest extends FlatSpec with ShouldMatchers {

  behavior of classOf[DateField[_]].getSimpleName

  it should "persist the record and on reading make it available as a Joda DateTime object" in {
    // Setup
    val fongo = new Fongo("mongo-server")
    val mongo = fongo.getMongo
    MongoDB.defineDb(DefaultMongoIdentifier, mongo, "date_field")

    // Given
    val persistedDate = new DateTime()

    // When
    DateFieldRecord.createRecord.createdAt(persistedDate).save

    // Then
    val allRecords = DateFieldRecord.findAll
    allRecords.length should equal(1)

    allRecords(0).createdAt.toDateTime should equal(persistedDate)
  }

}