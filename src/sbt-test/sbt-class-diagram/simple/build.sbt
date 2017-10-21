enablePlugins(ClassDiagramPlugin)

val scalazVersion = "7.2.16"

scalaVersion := "2.11.11"

libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion

def urlMap(clazz: Class[_]): Map[String, String] =
  if (clazz.getName.startsWith("scalaz"))
    Map(
      "href" -> s"https://github.com/scalaz/scalaz/tree/v${scalazVersion}/core/src/main/scala/${clazz.getName.replace('.', '/')}.scala",
      "tooltip" -> clazz.getName
    )
  else
    Map()

classDiagramSetting ~= { s =>
  s.copy(
    nodeSetting = clazz => s.nodeSetting(clazz) ++ urlMap(clazz),
    filter = { clazz =>
      clazz != classOf[java.lang.Object] && !clazz.getName.endsWith("Parent")
    },
    commonNodeSetting = s.commonNodeSetting + ("target" -> "_blank")
  )
}
