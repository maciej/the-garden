package com.softwaremill.thegarden.json4s.serializers

import org.json4s._

import scala.reflect.ClassTag

// TODO convert implicit param to a context bound
class JavaEnumNameSerializer[T <: Enum[T]](implicit ct: ClassTag[T]) extends Serializer[T] {

  import JsonDSL._

  private lazy val enumClass: Class[T] = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), T] = {
    case (ti: TypeInfo, v: JString) if ti.clazz.isEnum && ti.clazz == enumClass =>
      Enum.valueOf(enumClass, v.values)
  }

  private def mappingError(text: String) = throw new MappingException(text)

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case e: Enum[_] =>
      e.name()
  }

}
