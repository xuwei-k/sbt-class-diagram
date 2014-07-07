ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)
