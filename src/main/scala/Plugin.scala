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
    val classDiagramSetting = SettingKey[DiagramSetting]("classDiagramSetting", "http://www.graphviz.org/pdf/dotguide.pdf")
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
          val dot = Diagram(loader, classes.toList, classDiagramSetting.value)
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
    classDiagramSetting := classDiagramSetting.?.value.getOrElse{
      DiagramSetting(
        name = "diagram",
        commonNodeSetting = Map("shape" -> "record", "style" -> "filled"),
        commonEdgeSetting = Map("arrowtail" -> "none"),
        nodeSetting = clazz => Map("fillcolor" -> {if(clazz.isInterface) "#799F5A" else "#7996AC"}),
        edgeSetting = (_, _) => Map.empty,
        filter = _ != classOf[java.lang.Object]
      )
    },
    classDiagramWrite <<= writeTask
  )

  private[this] def createParser(classNames: Seq[String]): Parser[Seq[String]] = {
    classNames match {
      case Nil =>
        defaultParser
      case _ =>
        distinctParser(classNames.toSet)
    }
  }

  private[this] def distinctParser(exs: Set[String]): Parser[Seq[String]] = {
    val base = token(Space) ~> token(NotSpace examples exs)
    base.flatMap { ex =>
      val (_, notMatching) = exs.partition(GlobFilter(ex).accept)
      distinctParser(notMatching).map { result => ex +: result }
    } ?? Nil
  }
}

