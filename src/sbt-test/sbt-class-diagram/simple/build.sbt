classDiagramSettings

val scalazVersion = "7.1.3"

scalaVersion := "2.11.6"

libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion

def urlMap(clazz: Class[_]): Map[String, String] =
  if (clazz.getName.startsWith("scalaz"))
    Map(
      "href" -> s"https://github.com/scalaz/scalaz/tree/v${scalazVersion}/core/src/main/scala/${clazz.getName.replace('.', '/')}.scala",
      "tooltip" -> clazz.getName
    )
  else
    Map()

DiagramKeys.classDiagramSetting ~= { s =>
  s.copy(
    nodeSetting = clazz => s.nodeSetting(clazz) ++ urlMap(clazz),
    commonNodeSetting = s.commonNodeSetting + ("target" -> "_blank")
  )
}
