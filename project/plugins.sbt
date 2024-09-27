scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.11.3")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")

fullResolvers ~= {_.filterNot(_.name == "jcenter")}
