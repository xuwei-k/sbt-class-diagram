scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % "2.1.0"

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.2")
