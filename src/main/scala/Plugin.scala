package diagram

import sbt._, Keys._
import sbt.complete.Parsers

object Plugin extends sbt.Plugin {

  object DiagramKeys {
    val classDiagram = InputKey[Unit]("classDiagram")
    val fileName = SettingKey[String]("classDiagramFileName")
  }

  import DiagramKeys._

  val classDiagramSettings: Seq[Def.Setting[_]] = Seq(
    fileName := "classDiagram.svg",
    classDiagram := {
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

