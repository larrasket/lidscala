name := "lid-angle-sensor"
version := "0.1.0"
scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "net.java.dev.jna" % "jna" % "5.14.0",
  "net.java.dev.jna" % "jna-platform" % "5.14.0"
)

fork := true
