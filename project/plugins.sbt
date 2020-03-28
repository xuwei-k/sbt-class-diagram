scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

fullResolvers ~= {_.filterNot(_.name == "jcenter")}
