package com.softwaremill.thegarden.json4s.serializers

import org.json4s.{DefaultFormats, Formats}
import org.scalatest.{Matchers, FlatSpec}

class StandardizeFieldNamesSpec extends FlatSpec with Matchers {

  implicit private val formats: Formats = new DefaultFormats {} + StandardizeFieldNames

  import org.json4s.jackson.Serialization.{read, write}

  it should "standardize the field names of JSON fields to ones with underscore yet deserializing into Scala camelCase" in {
    val post = new BlogPost(10, "foo bar")

    val serialized = write(post)
    serialized should include("post_id")

    val passedThrough = read[BlogPost](serialized)

    passedThrough shouldEqual post
  }

}