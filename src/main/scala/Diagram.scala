package diagram

import java.io.File
import Reflect.getAllClassAndTrait

object Diagram {

  def apply(loader: ClassLoader, classNames: List[String], setting: DiagramSetting): String =
    dot2svg(dot(loader, classNames, setting))

  def dot(loader: ClassLoader, classNames: List[String], setting: DiagramSetting): String = {
    val classes = classNames.map { loader.loadClass }
    val list = {
      classes.flatMap { makeClassNodes } ::: classes.map { c =>
        ClassNode(c, Option(c.getSuperclass).toList ::: c.getInterfaces.toList)
      }
    }.distinct.filter { c => setting.filter(c.clazz) }
    ClassNode.dot(list, setting)
  }

  def dot2svg(dotString: String): String = withTmpDir { dir =>
    import sys.process.*
    val name = System.currentTimeMillis.toString
    val dotFile = new File(dir, name + ".dot")
    val svgFile = new File(dir, name + ".svg")
    sbt.IO.writeLines(dotFile, dotString :: Nil)
    Seq("dot", "-o" + svgFile.getAbsolutePath, "-Tsvg", dotFile.getAbsolutePath).!
    scala.io.Source.fromFile(svgFile).mkString
  }

  private def withTmpDir[T](action: File => T): T = {
    val dir = java.nio.file.Files.createTempDirectory(System.currentTimeMillis().toString).toFile
    try { action(dir) }
    finally { dir.delete() }
  }

  private def makeClassNodes(clazz: Class[?]): List[ClassNode] = {
    getAllClassAndTrait(clazz).map { x =>
      val interfaces = x.getInterfaces.toList
      val classes = Option(x.getSuperclass).map { _ :: interfaces }.getOrElse(interfaces)
      ClassNode(x, classes)
    }
  }

}
