import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object Dependencies {

  private val slf4jVersion = "1.7.6"
  val logging = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  )

  private val scalatestVersion = "2.1.7"
  val baseDependencies = Seq(
    "com.typesafe" % "config" % "1.2.0",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )

  val scalatestInCompileScope = "org.scalatest" %% "scalatest" % scalatestVersion

  val commonsIo = "commons-io" % "commons-io" % "2.4"

  private val fongoVersion = "1.5.1"
  val mongodbStack = Seq(
    "org.mongodb" % "mongo-java-driver" % "2.12.2",
    "com.github.fakemongo" % "fongo" % fongoVersion % "test",
    "org.mongodb" %% "casbah" % "2.7.2" exclude(org = "org.scala-lang", name = "scala-library")
  )

  val fongoInCompileScope = "com.github.fakemongo" % "fongo" % fongoVersion

  val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.1",
    "org.joda" % "joda-convert" % "1.2"
  )

  val akkaVersion = "2.3.4"
  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akka = Seq(akkaActors, akkaSlf4j, akkaTestKit)

  val json4sVersion = "3.2.10"
  val json4s = "org.json4s" %% "json4s-jackson" % json4sVersion
  val json4sExt = "org.json4s" %% "json4s-ext" % json4sVersion
  val json4sSeq = Seq(json4s)

  val json4sInProvidedScope = json4sSeq.map(_ % "provided")

  val jettyVersion = "8.1.8.v20121106"
  val servletApi = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" %
    "provided;test" artifacts Artifact("javax.servlet", "jar", "jar")

  val scalatraStack = Seq(
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
    servletApi
  ) ++ json4sSeq

  val sprayVersion = "1.3.1"
  val spray = Seq(
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test"
  )

  val sprayCan = "io.spray" %% "spray-can" % sprayVersion

  val sprayStack = Seq(
    json4s,
    json4sExt
  ) ++ spray ++ akka

  def inCompileScope(deps: Seq[ModuleID]): Seq[ModuleID] = deps.map(_.copy(configurations = Some("compile")))

}

object TheGardenBuild extends Build {

  import Dependencies._

  override val settings = super.settings ++ Seq(
    name := "the-garden",
    isSnapshot <<= isSnapshot or version(_ endsWith "-SNAPSHOT"))

  lazy val rootSettings: Seq[Setting[_]] = Seq(
    scalacOptions in GlobalScope in Compile := Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions in GlobalScope in Test := Seq("-unchecked", "-deprecation", "-feature"),
    // http://stackoverflow.com/questions/21435023/how-to-change-jdk-set-by-sbt-import-in-intellij-idea
    javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8"),
    scalaVersion := "2.11.2",
    crossScalaVersions := Seq("2.10.2", "2.11.2"),
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
  ) ++ releaseSettings

  lazy val lawn = Project(id = "lawn",
    base = file("lawn"),
    settings = rootSettings).settings(
      libraryDependencies ++= jodaTime ++ logging ++ Seq(commonsIo)
    )

  lazy val shrubs = Project(id = "shrubs",
    base = file("shrubs"),
    settings = rootSettings).settings(
      libraryDependencies ++= Seq(scalatestInCompileScope) ++ jodaTime ++ json4sInProvidedScope
    ) dependsOn lawn

  lazy val mongodb = Project(id = "mongodb",
    base = file("mongodb"),
    settings = rootSettings).settings(
      libraryDependencies ++= mongodbStack ++ logging
    )

  lazy val mongodbTest = Project(id = "mongodb-test",
    base = file("mongodb-test"),
    settings = rootSettings).settings(
      libraryDependencies ++= mongodbStack ++ logging ++ Seq(scalatestInCompileScope, fongoInCompileScope)
    )


  lazy val gardenScalatra = Project(id = "garden-scalatra",
    base = file("garden-scalatra"),
    settings = rootSettings).settings(
      libraryDependencies ++= scalatraStack
    ) dependsOn lawn

  lazy val gardenSpray = Project(id = "garden-spray",
    base = file("garden-spray"),
    settings = rootSettings).settings(
      libraryDependencies ++= sprayStack
    ) dependsOn lawn

  lazy val gardenSprayTestkit = Project(id = "garden-spray-testkit",
    base = file("garden-spray-testkit"),
    settings = rootSettings).settings(
      libraryDependencies ++= sprayStack ++ Seq(scalatestInCompileScope, sprayCan) ++ inCompileScope(akka)
    ) dependsOn lawn

  lazy val gardenJson4s = Project(id = "garden-json4s",
    base = file("garden-json4s"),
    settings = rootSettings).settings(
      libraryDependencies ++= json4sSeq
    )

  lazy val theGarden = Project(id = "the-garden",
    base = file(""),
    settings = rootSettings).aggregate(lawn, mongodb, shrubs, mongodbTest, gardenScalatra,
      gardenSpray, gardenSprayTestkit, gardenJson4s)

}
