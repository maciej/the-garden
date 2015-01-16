package com.softwaremill.thegarden.json4s.serializers

import org.json4s.{DefaultFormats, Formats}
import org.scalatest.{Matchers, FlatSpec}

class CamelCaseFieldNameDeserializerSpec extends FlatSpec with Matchers {
  implicit private val formats: Formats = new DefaultFormats {} + CamelCaseFieldNameDeserializer

  val SerializedBlogPost = """{"post_id":10,"text":"foo bar"}"""

  import org.json4s.jackson.Serialization.read

  it should "change the serialization format in a way that field names with underscores fields are converted to camelCase" in {
    val post = read[BlogPost](SerializedBlogPost)

    post.postId shouldEqual 10
    post.text shouldEqual "foo bar"
  }
}
