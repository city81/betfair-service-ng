logLevel := Level.Debug

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Maven 2" at "http://repo2.maven.org/maven2"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

resolvers += "Sonatype Repository" at "https://oss.sonatype.org/content/groups/public"

resolvers += "HttpClient" at "http://repo.springsource.org/libs-milestone/org/apache/httpcomponents/httpclient/4.3.6/"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.6.4")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.4")