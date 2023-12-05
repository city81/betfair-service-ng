name := "betfair-service-ng"

organization := "betfair"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.1"

scalacOptions := Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-language:reflectiveCalls",
  "-Xmax-classfile-name", "128"
)

// sbt-Revolver allows the running of the spray service in sbt in the background using re-start
seq(Revolver.settings: _*)

mainClass in(Compile, run) := Some("com.betfair.service.Boot")

packageOptions in(Compile, packageBin) +=
  Package.ManifestAttributes(java.util.jar.Attributes.Name.MAIN_CLASS -> "com.betfair.service.Boot")

assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter { x => x.data.getName.matches("specs2_2.11-2.3.13.jar") || x.data.getName.matches("mockito-core-1.9.5.jar") }
}

libraryDependencies ++= {
  Seq(
    "com.github.tomakehurst" % "wiremock" % "1.46" % "test",
    "com.typesafe.akka" % "akka-slf4j_2.12" % "2.4.17",
    "com.typesafe.akka" % "akka-http_2.12" % "10.0.0",
    "ch.qos.logback" % "logback-classic" % "1.1.0",
    "org.scalatest" % "scalatest_2.12" % "3.0.1",
    "org.mockito" % "mockito-all" % "1.9.5" % "provided",
    "org.mockito" % "mockito-core" % "1.9.5" % "provided",
    "com.github.nscala-time" %% "nscala-time" % "2.16.0",
    "org.apache.httpcomponents" % "httpclient" % "4.3.6",
    "org.codehaus.plexus" % "plexus-utils" % "1.5.7",
    "com.typesafe.play" % "play-json_2.12" % "2.6.0-M1",
    "com.github.nscala-time" %% "nscala-time" % "2.16.0",
    "joda-time" % "joda-time" % "2.9.6",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.27.0",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test,
    "org.scalaj" %% "scalaj-http" % "2.4.1"


  )
}

credentials += Credentials(Path.userHome / ".sbt/.credentials")
