package diagram

import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser
import xsbti.api.ClassLike

object Plugin extends sbt.Plugin {

  object DiagramKeys {
    val classDiagram = InputKey[File]("classDiagram", "write svg file and open")
    val classDiagramWrite = InputKey[File]("classDiagramWrite", "write svg file")
    val fileName = SettingKey[String]("classDiagramFileName")
    val classNames = TaskKey[Seq[String]]("classDiagramClassNames")
  }

  import diagram.Plugin.DiagramKeys._
  import sbinary.DefaultProtocol._
  import sbt.Cache.seqFormat

  private[this] val defaultParser: Parser[Seq[String]] =
    (Space ~> token(StringBasic, "<class name>")).*

  private[this] val writeTask: Def.Initialize[InputTask[File]] =
    InputTask.createDyn(
      Defaults.loadForParser(classNames)(
       (state, classes) => classes.fold(defaultParser)(createParser)
      )
    ){
      Def.task{ classes: Seq[String] =>
        Def.task{
          val loader = (testLoader in Test).value
          val clazz = loader.loadClass(classes.head)
          val dot = Diagram(loader, classes.toList)
          val svg = Keys.target.value / fileName.value
          IO.writeLines(svg, dot :: Nil)
          svg
        }
      }
    }

  val classDiagramSettings: Seq[Def.Setting[_]] = Seq(
    classNames := Tests.allDefs((compile in Compile).value).collect{ case c: ClassLike => c.name },
    classNames <<= classNames storeAs classNames triggeredBy (compile in Compile),
    fileName := "classDiagram.svg",
    classDiagram <<= writeTask.map{ svg =>
      java.awt.Desktop.getDesktop.open(svg)
      svg
    },
    classDiagramWrite <<= writeTask
  )

  private def createParser(classNames: Seq[String]): Parser[Seq[String]] = {
    classNames match {
      case Nil =>
        defaultParser
      case _   =>
        val other = Space ~> token(StringBasic, _ => true)
        ((Space ~> classNames.distinct.map(token(_)).reduce(_ | _)) | other).*
    }
  }
}

