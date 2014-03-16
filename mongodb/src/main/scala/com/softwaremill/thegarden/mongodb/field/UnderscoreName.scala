package com.softwaremill.thegarden.mongodb.field

import net.liftweb.record.BaseField

/**
 * Use this trait to automatically set the {@code name} of the field,
 * to the "fiendName" converted into the "underscore_notation", that should be used in mongo.
 * <b>Example:</b> Given a field with the name: "userId" the name in mongo would become "user_id".
 *
 * An usage example would be:
 * <code>
 * object userId extends StringField(this, 100) { override def name = "user_id" }
 * </code>
 *
 * can be replaced with:
 * <code>
 * object userId extends StringField(this, 100) with UnderscoreName
 * </code>
 */
trait UnderscoreName extends BaseField {
  override lazy val name: String = UnderscoreName.fromCamelCase(super.name)
}

object UnderscoreName {
  val UppercaseLetter = "([A-Z])".r

  def fromCamelCase(camelCase: String): String =
    if (camelCase == null) ""
    else camelCase.toCharArray.map(_.toString).map(_ match {
      case UppercaseLetter(l) => "_" + l.toLowerCase
      case "-" => "_"
      case s => s
    }).mkString
}
