classDiagramSettings

val scalazVersion = "7.1.1"

scalaVersion := "2.11.6"

libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion

val urlMapping: PartialFunction[Class[_], String] = {
  case clazz if clazz.getName.startsWith("scalaz") =>
    s"https://github.com/scalaz/scalaz/tree/v${scalazVersion}/core/src/main/scala/${clazz.getName.replace('.', '/')}.scala"
}

DiagramKeys.classDiagramSetting ~= { s =>
  s.copy(
    nodeSetting = clazz => s.nodeSetting(clazz) ++ urlMapping.lift(clazz).map("href" -> _).toList,
    commonNodeSetting = s.commonNodeSetting + ("target" -> "_blank")
  )
}
