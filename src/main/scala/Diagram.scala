package diagram

import java.io.File
import Reflect.getAllClassAndTrait

object Diagram {

  def apply(loader: ClassLoader, classNames: List[String]): String = {
    val classes = classNames.map{loader.loadClass}
    val list = {
      classes.flatMap{makeClassNodes} ::: classes.map { c =>
        ClassNode(c, Option(c.getSuperclass).toList ::: c.getInterfaces.toList)
      }
    }.distinct.filter{ c => !ClassNode.exceptList.contains(c.clazz) }
    val d = ClassNode.dot(list)
    withTmpDir{ dir =>
      import sys.process._
      val name = System.currentTimeMillis.toString
      val dotFile = new File(dir, name + ".dot")
      val svgFile = new File(dir, name + ".svg")
      java.nio.file.Files.write(dotFile.toPath, java.util.Collections.singleton(d))
      Seq("dot", "-o" + svgFile.getAbsolutePath, "-Tsvg", dotFile.getAbsolutePath).!
      scala.io.Source.fromFile(svgFile).mkString
    }
  }

  private def withTmpDir[T](action: File => T): T = {
    val dir = java.nio.file.Files.createTempDirectory(System.currentTimeMillis().toString).toFile
    try { action(dir) }
    finally { dir.delete() }
  }

  private def makeClassNodes(clazz: Class[_]): List[ClassNode] = {
    getAllClassAndTrait(clazz).map { x =>
      val interfaces = x.getInterfaces.toList
      val classes = Option(x.getSuperclass).map{_ :: interfaces }.getOrElse(interfaces)
      ClassNode(x, classes)
    }
  }

}
