name := "scalajs"

version := "1.0"

scalaVersion := "2.11.6"

enablePlugins(ScalaJSPlugin)

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.9.0"

jsDependencies += "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React"