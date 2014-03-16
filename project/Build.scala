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

  val baseDependencies = Seq(
    "org.scalatest" %% "scalatest" % "2.0" % "test",
    "com.typesafe" % "config" % "1.2.0",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )

  /* Mongo, Lift-Record, Rogue, Casbah */
  private val casbah = "org.mongodb" %% "casbah" % "2.6.5" exclude(org = "org.scala-lang", name = "scala-library")
  //  private val rogueVersion = "2.2.0"
  val mongodbStack = Seq(
    "net.liftweb" %% "lift-mongodb-record" % "2.5.1",
    "org.mongodb" % "mongo-java-driver" % "2.11.4",
    "com.github.fakemongo" % "fongo" % "1.3.7" % "test"
  ) ++ Seq(
    casbah
  )

}

object TheGardenBuild extends Build {

  import Dependencies._

  override val settings = super.settings ++ Seq(isSnapshot <<= isSnapshot or version(_ endsWith "-SNAPSHOT"))

  lazy val rootSettings: Seq[Setting[_]] = Project.defaultSettings ++ Seq(
    version := "0.0.2-SNAPSHOT",
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
    homepage := Some(new java.net.URL("http://www.softwaremill.com"))
  )

  lazy val lawn = Project(id = "lawn",
    base = file("lawn"),
    settings = rootSettings)

  lazy val mongodb = Project(id = "mongodb",
    base = file("mongodb"),
    settings = rootSettings).settings(
      libraryDependencies ++= baseDependencies ++ mongodbStack ++ logging
    ).
    settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

}
