package com.softwaremill.thegarden.json4s.serializers

import org.json4s.{DefaultFormats, Formats}
import org.scalatest.{FlatSpec, Matchers}

private[serializers] case class BlogPost(postId: Int, text: String)

class UnderscorizeFieldNamesSerializerSpec extends FlatSpec with Matchers {

  implicit private val formats: Formats = new DefaultFormats {} + UnderscorizeFieldNamesSerializer

  import org.json4s.jackson.Serialization.write

  it should "change the serialization format such that camelCase fields are serialized with an underscore" in {
    val post = new BlogPost(10, "foo bar")

    val serialized = write(post)

    serialized should not include "postId"
    serialized should include("post_id")
  }

}

