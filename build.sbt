name := "the-garden"

organization in GlobalScope := "com.typesafe"

scalaVersion in ThisBuild := "2.10.3"

scalacOptions in GlobalScope in Compile := Seq("-unchecked", "-deprecation", "-feature")

scalacOptions in GlobalScope in Test := Seq("-unchecked", "-deprecation", "-feature")

// http://stackoverflow.com/questions/21435023/how-to-change-jdk-set-by-sbt-import-in-intellij-idea
javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7")
