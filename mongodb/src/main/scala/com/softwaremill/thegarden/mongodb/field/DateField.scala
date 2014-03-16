package com.softwaremill.thegarden.mongodb.field

import net.liftweb.mongodb.record.BsonRecord
import org.joda.time.DateTime

class DateField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType)
  extends net.liftweb.mongodb.record.field.DateField(rec){

  def apply(dateTime : DateTime) : OwnerType = apply(dateTime.toDate)

  def toDateTime = new DateTime(get)
}

class OptionalDateField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType)
  extends net.liftweb.mongodb.record.field.DateField(rec) {

  def apply(dateTime: DateTime) : OwnerType = apply(Some(dateTime.toDate))

  def apply(dateTimeOpt: Option[DateTime]) : OwnerType = apply(dateTimeOpt.map(_.toDate))

  override def optional_? = true

  override def defaultValue = null

  def toDateTime : Option[DateTime] = valueBox.map(b => new DateTime(b.getTime))

}
