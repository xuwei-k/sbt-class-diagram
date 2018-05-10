ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

val javaVmArgs: List[String] = {
  import scala.collection.JavaConverters._
  java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
}

scriptedLaunchOpts ++= javaVmArgs.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

scriptedLaunchOpts ++= javaVmArgs.filter(
  a => Seq("scala.ext.dirs").exists(a.contains)
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)
