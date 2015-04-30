import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.SbtGit.git
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._
import bintray.Plugin._

object Dependencies {

  private val slf4jVersion = "1.7.10"
  val logging = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  )

  private val scalatestVersion = "2.2.2"
  val baseDependencies = Seq(
    "com.typesafe" % "config" % "1.2.0",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )

  val scalatestForTestingModules = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % "provided",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  )

  val commonsIo = "commons-io" % "commons-io" % "2.4"
  // https://code.google.com/p/concurrentlinkedhashmap/
  val concurrentLinkedHashMap = "com.googlecode.concurrentlinkedhashmap" % "concurrentlinkedhashmap-lru" % "1.4.1"

  private val fongoVersion = "1.5.7"
  val mongodbStack = Seq(
    "org.mongodb" % "mongo-java-driver" % "2.12.2",
    "com.github.fakemongo" % "fongo" % fongoVersion % "test",
    "org.mongodb" %% "casbah" % "2.7.4" exclude(org = "org.scala-lang", name = "scala-library")
  )

  val fongoInCompileScope = "com.github.fakemongo" % "fongo" % fongoVersion

  val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7"
  )

  val akkaVersion = "2.3.8"
  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akka = Seq(akkaActors, akkaSlf4j, akkaTestKit)

  val json4sVersion = "3.2.11"
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

  val sprayVersion = "1.3.2"
  val spray = Seq(
    "io.spray" %% "spray-routing-shapeless2" % sprayVersion,
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
    organization := "me.maciej.garden",
    name := "the-garden",
    isSnapshot <<= isSnapshot or version(_ endsWith "-SNAPSHOT")
  )

  val scalaVersion = "2.11.6"

  // Reading material
  // http://www.scala-sbt.org/0.13/docs/Combined+Pages.html#Define+the+repository
  // http://www.jfrog.com/confluence/pages/viewpage.action?pageId=26083425#DeployingMavenandGradlesnapshotstoOJO%28oss.jfrog.org%29-onboard

  // No JFrog OSS repository yet
  //    publishTo := Some("Artifactory Realm" at "http://oss.jfrog.org/artifactory/oss-snapshot-local"),
  //    credentials := Credentials(Path.userHome / ".bintray" / ".artifactory") :: Nil

  val publishingSettings: Seq[Setting[_]] = bintrayPublishSettings ++ Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomExtra :=
      <developers>
        <developer>
          <id>maciej</id>
          <name>Maciej Bilas</name>
          <url>http://twitter.com/maciejb</url>
        </developer>
      </developers>,
    licenses := ("Apache-2.0", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    homepage := Some(new java.net.URL("http://maciejb.me")),
    bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("maciej"),
    git.useGitDescribe := true
  )

  lazy val rootSettings: Seq[Setting[_]] = Seq(
    scalacOptions in GlobalScope in Compile := Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions in GlobalScope in Test := Seq("-unchecked", "-deprecation", "-feature"),
    // http://stackoverflow.com/questions/21435023/how-to-change-jdk-set-by-sbt-import-in-intellij-idea
    javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8"),
    Keys.scalaVersion := scalaVersion,
    ReleaseKeys.crossBuild := false,
    crossScalaVersions := Seq(scalaVersion),
    libraryDependencies ++= baseDependencies
  ) ++ releaseSettings

  def GardenProject(id: String, base: File,
                    aggregate: => Seq[ProjectReference] = Nil,
                    dependencies: => Seq[ClasspathDep[ProjectReference]] = Nil,
                    delegates: => Seq[ProjectReference] = Nil,
                    settings: => Seq[Def.Setting[_]] = rootSettings ++ publishingSettings,
                    configurations: Seq[Configuration] = Nil,
                    auto: AddSettings = AddSettings.allDefaults) =
    Project(id, base, aggregate, dependencies, delegates, settings, configurations, auto)

  lazy val lawn = GardenProject(id = "garden-lawn",
    base = file("lawn")).settings(
      libraryDependencies ++= jodaTime ++ logging ++ Seq(commonsIo, concurrentLinkedHashMap)
    )

  lazy val shrubs = GardenProject(id = "garden-shrubs",
    base = file("shrubs")).settings(
      libraryDependencies ++= scalatestForTestingModules ++ jodaTime ++ json4sInProvidedScope
    ) dependsOn lawn

  lazy val mongodb = GardenProject(id = "garden-mongodb",
    base = file("mongodb")).settings(
      libraryDependencies ++= mongodbStack ++ logging
    )

  lazy val mongodbTest = GardenProject(id = "garden-mongodb-test",
    base = file("mongodb-test")).settings(
      libraryDependencies ++= mongodbStack ++ logging ++ scalatestForTestingModules ++ Seq(fongoInCompileScope)
    )


  lazy val gardenScalatra = GardenProject(id = "garden-scalatra",
    base = file("garden-scalatra")).settings(
      libraryDependencies ++= scalatraStack
    ) dependsOn lawn

  lazy val gardenSpray = GardenProject(id = "garden-spray",
    base = file("garden-spray")).settings(
      libraryDependencies ++= sprayStack
    ) dependsOn lawn

  lazy val gardenSprayTestkit = GardenProject(id = "garden-spray-testkit",
    base = file("garden-spray-testkit")).settings(
      libraryDependencies ++= sprayStack ++ scalatestForTestingModules ++ Seq(sprayCan) ++ inCompileScope(akka)
    ) dependsOn lawn

  lazy val gardenJson4s = GardenProject(id = "garden-json4s",
    base = file("garden-json4s")).settings(
      libraryDependencies ++= json4sSeq
    )

  lazy val gardenAkka = GardenProject(id = "garden-akka",
    base = file("garden-akka")).settings(
      libraryDependencies ++= akka
    ) dependsOn lawn

  lazy val theGarden = GardenProject(id = "the-garden",
    base = file("")).aggregate(lawn, mongodb, shrubs, mongodbTest, gardenScalatra,
      gardenSpray, gardenSprayTestkit, gardenJson4s, gardenAkka)

}
