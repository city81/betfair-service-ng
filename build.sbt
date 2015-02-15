name := "betfair-service-ng"

organization := "betfair"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions := Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-language:reflectiveCalls",
  "-Xmax-classfile-name", "128"
)

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

// sbt-Revolver allows the running of the spray service in sbt in the background using re-start
seq(Revolver.settings: _*)

mainClass in (Compile,run) := Some("com.betfair.service.Boot")

packageOptions in (Compile, packageBin) +=
  Package.ManifestAttributes( java.util.jar.Attributes.Name.MAIN_CLASS -> "com.betfair.service.Boot" )

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "com.github.tomakehurst" % "wiremock" % "1.46" % "test",
    "io.spray" % "spray-can_2.11" % sprayV,
    "io.spray" % "spray-caching_2.11" % sprayV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "io.spray" % "spray-client_2.11" % sprayV,
    "io.spray" % "spray-routing_2.11" % sprayV,
    "io.spray" % "spray-testkit_2.11" % sprayV,
    "io.spray" %% "spray-json" % "1.3.1",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "ch.qos.logback" % "logback-classic" % "1.1.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "com.github.nscala-time" %% "nscala-time" % "1.4.0",
    "com.typesafe.play" %% "play-json" % "2.4.0-M1",
    "org.apache.httpcomponents" % "httpclient" % "4.3.6",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.1" % "test"
  )
}

credentials += Credentials(Path.userHome / ".sbt/.credentials")