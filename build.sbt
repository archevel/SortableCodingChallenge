name := "SortableChallenge"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "snapshots" at "http://scala-tools.org/repo-snapshots"

resolvers += "releases"  at "http://scala-tools.org/repo-releases"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.7.1" % "test",
  "net.liftweb" %% "lift-json" % "2.4"
)

fork in run := true

javaOptions in run += "-Xmx2G"
