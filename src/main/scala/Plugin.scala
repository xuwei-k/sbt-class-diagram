package diagram

import sbt._, Keys._
import sbt.complete.Parsers

object Plugin extends sbt.Plugin {

  object DiagramsKeys {
    val classDiagrams = InputKey[Unit]("classDiagrams")
    val fileName = SettingKey[String]("classDiagramsFileName")
  }

  import DiagramsKeys._

  val classDiagramsSettings: Seq[Def.Setting[_]] = Seq(
    fileName := "classDiagrams.svg",
    classDiagrams := {
      val classes = Parsers.spaceDelimited("<class names>").parsed
      val loader = (testLoader in Test).value
      val clazz = loader.loadClass(classes.head)
      val dot = Diagram(loader, classes.toList)
      val svg = Keys.target.value / fileName.value
      IO.writeLines(svg, dot :: Nil)
      java.awt.Desktop.getDesktop.open(svg)
    }
  )

}

