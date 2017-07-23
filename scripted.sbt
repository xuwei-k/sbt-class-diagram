ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("scala.ext.dirs").exists(a.contains)
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)
