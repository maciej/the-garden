import sbt._
import sbt.Keys._

object Dependencies {

  private val slf4jVersion = "1.7.6"
  val logging = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe" %% "scalalogging-slf4j" % "1.1.0"
  )

  private val scalatestVersion = "2.0"
  val baseDependencies = Seq(
    "com.typesafe" % "config" % "1.2.0",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )
  val scalatestInCompileScope = "org.scalatest" %% "scalatest" % scalatestVersion

  private val fongoVersion = "1.3.7"
  val mongodbStack = Seq(
    "net.liftweb" %% "lift-mongodb-record" % "2.5.1",
    "org.mongodb" % "mongo-java-driver" % "2.11.4",
    "com.github.fakemongo" % "fongo" % fongoVersion % "test",
    "org.mongodb" %% "casbah" % "2.6.5" exclude(org = "org.scala-lang", name = "scala-library")
  )

  val fongoInCompileScope = "com.github.fakemongo" % "fongo" % fongoVersion

  val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.1",
    "org.joda" % "joda-convert" % "1.2"
  )

  val json4sVersion = "3.2.8"
  val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % json4sVersion
  )
  val json4sInProvidedScope = json4s.map(_ % "provided")
}

object TheGardenBuild extends Build {

  import Dependencies._

  override val settings = super.settings ++ Seq(
    name := "the-garden",
    isSnapshot <<= isSnapshot or version(_ endsWith "-SNAPSHOT"))

  lazy val rootSettings: Seq[Setting[_]] = Project.defaultSettings ++ Seq(
    scalacOptions in GlobalScope in Compile := Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions in GlobalScope in Test := Seq("-unchecked", "-deprecation", "-feature"),
    // http://stackoverflow.com/questions/21435023/how-to-change-jdk-set-by-sbt-import-in-intellij-idea
    javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7"),
    version := "0.0.10-SNAPSHOT",
    scalaVersion := "2.10.3",
    organization := "com.softwaremill.thegarden",
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://nexus.softwaremill.com/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "content/repositories/releases")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomExtra := <scm>
      <url>git@github.com:maciej/the-garden.git</url>
      <connection>scm:git:git@github.com:maciej/the-garden.git</connection>
    </scm>
      <developers>
        <developer>
          <id>maciej</id>
          <name>Maciej Bilas</name>
          <url>http://twitter.com/maciejb</url>
        </developer>
      </developers>,
    licenses := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    homepage := Some(new java.net.URL("http://www.softwaremill.com")),
    libraryDependencies ++= baseDependencies
  )

  lazy val lawn = Project(id = "lawn",
    base = file("lawn"),
    settings = rootSettings).settings(
      libraryDependencies ++= jodaTime
    )

  lazy val shrubs = Project(id = "shrubs",
    base = file("shrubs"),
    settings = rootSettings).settings(
      libraryDependencies ++= Seq(scalatestInCompileScope) ++ jodaTime ++ json4sInProvidedScope
    )

  lazy val mongodb = Project(id = "mongodb",
    base = file("mongodb"),
    settings = rootSettings).settings(
      libraryDependencies ++= mongodbStack ++ logging
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  lazy val mongodbTest = Project(id = "mongodb-test",
    base = file("mongodb-test"),
    settings = rootSettings).settings(
      libraryDependencies ++= mongodbStack ++ logging ++ Seq(scalatestInCompileScope, fongoInCompileScope)
    )

  lazy val theGarden = Project(id = "the-garden",
    base = file(""),
    settings = rootSettings).aggregate(lawn, mongodb, shrubs, mongodbTest)

}
