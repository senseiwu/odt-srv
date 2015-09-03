

packageArchetype.java_application

name := """odt-srv"""

version := "1.0"

scalaVersion  := "2.11.6"

resolvers += "spray repo" at "http://repo.spray.io"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"       % sprayV,
    "io.spray"            %%  "spray-routing"   % sprayV,
    "io.spray"            %%  "spray-client"    % sprayV,
    "io.spray"            %%  "spray-json"      % "1.3.2",
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"      % akkaV,
    "com.typesafe.slick"  %%  "slick"           % "3.0.0",
    "org.slf4j"           %   "slf4j-nop"       % "1.6.4",
    "ch.qos.logback"      %   "logback-classic" % "1.1.3",
    "com.h2database"      %   "h2"              % "1.3.176",
    "org.json4s"          %%  "json4s-native"   % "3.2.11",
    "com.google.guava"    %   "guava"           % "18.0",
    "joda-time"           %   "joda-time"       % "2.8.1"
  )
}

libraryDependencies ++= {
  Seq(
    "org.mongodb" % "casbah-core_2.11" % "2.8.2",
    "com.thoughtworks.xstream" % "xstream" % "1.4.8"
  )
}

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-testkit" % sprayV   % "test",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV    % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.scalatest"       %%  "scalatest"     % "2.2.5"  % "test"
  )
}