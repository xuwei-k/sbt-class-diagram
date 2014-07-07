scalacOptions ++= Seq("-deprecation", "-Xlint", "-unchecked", "-language:_")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.2")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.2")
