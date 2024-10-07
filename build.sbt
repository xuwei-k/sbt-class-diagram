sbtPlugin := true

name := "sbt-class-diagram"

organization := "com.github.xuwei-k"

startYear := Some(2014)

scriptedBatchExecution := false

Test / fork := true

Test / test := {
  if (scalaBinaryVersion.value == "3") {
    // https://github.com/sbt/sbt/issues/7724
    ()
  } else {
    (Test / test).value
  }
}

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)

watchSources ++= sbtTestDirectory.value.allPaths.get

def gitHash: String = sys.process.Process("git rev-parse HEAD").lineStream_!.head

Compile / doc / scalacOptions ++= {
  val tag = if (isSnapshot.value) gitHash else { "v" + version.value }
  Seq(
    "-sourcepath",
    baseDirectory.value.getAbsolutePath,
    "-doc-source-url",
    s"https://github.com/xuwei-k/sbt-class-diagram/tree/${tag}€{FILE_PATH}.scala"
  )
}

pomExtra := (
  <url>https://github.com/xuwei-k/sbt-class-diagram</url>
<developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
<scm>
  <url>git@github.com:xuwei-k/sbt-class-diagram.git</url>
  <connection>scm:git:git@github.com:xuwei-k/sbt-class-diagram.git</connection>
  <tag>{if (isSnapshot.value) gitHash else { "v" + version.value }}</tag>
</scm>
)

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % "test"
testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

sbtPluginPublishLegacyMavenStyle := {
  sys.env.isDefinedAt("GITHUB_ACTION") || isSnapshot.value
}
