package diagram

import sbt._, Keys._
import sbt.complete.Parsers

object Plugin extends sbt.Plugin {

  object DiagramKeys {
    val classDiagram = InputKey[File]("classDiagram", "write svg file and open")
    val classDiagramWrite = InputKey[File]("classDiagramWrite", "write svg file")
    val fileName = SettingKey[String]("classDiagramFileName")
  }

  import DiagramKeys._

  private[this] val writeTask: Def.Initialize[InputTask[File]] = Def.inputTask{
    val classes = Parsers.spaceDelimited("<class names>").parsed
    val loader = (testLoader in Test).value
    val clazz = loader.loadClass(classes.head)
    val dot = Diagram(loader, classes.toList)
    val svg = Keys.target.value / fileName.value
    IO.writeLines(svg, dot :: Nil)
    svg
  }

  val classDiagramSettings: Seq[Def.Setting[_]] = Seq(
    fileName := "classDiagram.svg",
    classDiagram <<= writeTask.map{ svg =>
      java.awt.Desktop.getDesktop.open(svg)
      svg
    },
    classDiagramWrite <<= writeTask
  )

}

