ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("scala.ext.dirs").exists(a.contains)
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)

// https://github.com/sbt/sbt/issues/3245
ScriptedPlugin.scripted := Def.inputTask {
  val args = ScriptedPlugin.asInstanceOf[{
    def scriptedParser(f: File): complete.Parser[Seq[String]]
  }].scriptedParser(sbtTestDirectory.value).parsed
  val prereq: Unit = scriptedDependencies.value
  try {
    if((sbtVersion in pluginCrossBuild).value == "1.0.0-M6") {
      ScriptedPlugin.scriptedTests.value.asInstanceOf[{
        def run(x1: File, x2: Boolean, x3: Array[String], x4: File, x5: Array[String], x6: java.util.List[File]): Unit
      }].run(
        sbtTestDirectory.value,
        scriptedBufferLog.value,
        args.toArray,
        sbtLauncher.value,
        scriptedLaunchOpts.value.toArray,
        new java.util.ArrayList()
      )
    } else {
      ScriptedPlugin.scriptedTests.value.asInstanceOf[{
        def run(x1: File, x2: Boolean, x3: Array[String], x4: File, x5: Array[String]): Unit
      }].run(
        sbtTestDirectory.value,
        scriptedBufferLog.value,
        args.toArray,
        sbtLauncher.value,
        scriptedLaunchOpts.value.toArray
      )
    }
  } catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }
}.evaluated
