organization := "org.consensusresearch"

name := "scorex"

version := "1.0.3"

scalaVersion := "2.11.7"

resolvers ++= Seq("Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Typesafe maven releases" at "http://repo.typesafe.com/typesafe/maven-releases/")

libraryDependencies ++= Seq(
  "org.mapdb" % "mapdb" % "1.+",
  "com.typesafe.play" %% "play-json" % "2.+",
  "com.typesafe.akka" %% "akka-actor" % "2.+",
  "io.spray" %% "spray-routing" % "1.+",
  "io.spray" %% "spray-can" % "1.+",
  "io.spray" %% "spray-http" % "1.+",
  "io.spray" %% "spray-httpx" % "1.+",
  "io.spray" %% "spray-util" % "1.+",
  "com.google.guava" % "guava" % "15.+",
  "commons-net" % "commons-net" % "3.+",
  "ch.qos.logback" % "logback-classic" % "1.+",
  "ch.qos.logback" % "logback-core" % "1.+",

  //dependencies for testing:
  "org.scalatest" %% "scalatest" % "2.+" % "test"
)


assemblyJarName in assembly := "scorex.jar"

test in assembly := {}

mainClass in assembly := Some("scorex.Start")