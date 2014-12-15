package com.softwaremill.thegarden.json4s.serializers

import org.json4s.{DefaultFormats, Formats}
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag

class JavaEnumNameSerializerSpec extends FlatSpec with Matchers {

  val v: ClassTag[BlogPostStatus] = ClassTag[BlogPostStatus](classOf[BlogPostStatus])

  implicit val formats: Formats = new DefaultFormats {} + new JavaEnumNameSerializer()(v)

  import org.json4s.jackson.Serialization.{write, read}

  it should "serialize a Java Enum" in {
    write(BlogPostStatus.PUBLISHED) shouldEqual "\"PUBLISHED\""
  }

  it should "deserialize a Java Enum" in {
    read[BlogPostStatus]("\"PUBLISHED\"") shouldEqual BlogPostStatus.PUBLISHED
  }

}
