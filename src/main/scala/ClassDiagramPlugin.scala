package diagram

import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser
import xsbti.api.ClassLike

import scala.reflect.NameTransformer

object ClassDiagramPlugin extends AutoPlugin {

  object autoImport {
    val classDiagram = InputKey[File]("classDiagram", "write svg file and open")
    val classDiagramDot = InputKey[String]("classDiagramDot")
    val classDiagramSVG = InputKey[String]("classDiagramSVG")
    val classDiagramWrite = InputKey[File]("classDiagramWrite", "write svg file")
    val classDiagramFileName = SettingKey[String]("classDiagramFileName", "svg file name")
    val classDiagramClassNames = TaskKey[Seq[String]]("classDiagramClassNames")
    val classDiagramSetting = SettingKey[DiagramSetting]("classDiagramSetting", "https://www.graphviz.org/pdf/dotguide.pdf")
  }

  import autoImport._
  import Serialization.Implicits._

  private[this] val defaultParser: Parser[Seq[String]] =
    (Space ~> token(StringBasic, "<class name>")).*

  private[this] def dotTask[A](f: String => Def.Initialize[Task[A]]): Def.Initialize[InputTask[A]] =
    InputTask.createDyn(
      Defaults.loadForParser(classDiagramClassNames)(
       (state, classes) => classes.fold(defaultParser)(createParser)
      )
    ){
      Def.task{
        val loader = (testLoader in Test).value
        val diagramSetting = classDiagramSetting.value

        (classes0: Seq[String]) => {
          val classes = classes0.map(
            _.split('.').map(NameTransformer.encode).mkString(".")
          )
          val clazz = loader.loadClass(classes.head)
          val dot = Diagram.dot(loader, classes.toList, diagramSetting)
          f(dot)
        }
      }
    }

  private[this] def svgTask[A](f: String => Def.Initialize[Task[A]]): Def.Initialize[InputTask[A]] =
    dotTask(dot => f(Diagram.dot2svg(dot)))

  private[this] val writeSVGTask = svgTask{ svg =>
    Def.task{
      val svgFile = Keys.target.value / classDiagramFileName.value
      IO.writeLines(svgFile, svg :: Nil)
      svgFile
    }
  }

  override val projectSettings: Seq[Def.Setting[_]] = Seq(
    classDiagramClassNames := Tests.allDefs((compile in Compile).value).collect{
      case c: ClassLike => ClassNode.decodeClassName(c.name)
    },
    classDiagramClassNames := (classDiagramClassNames storeAs classDiagramClassNames triggeredBy (compile in Compile)).value,
    classDiagramFileName := classDiagramFileName.?.value.getOrElse("classDiagram.svg"),
    classDiagram := {
      val svg = writeSVGTask.evaluated
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
    classDiagramDot := dotTask(Def.task(_)).evaluated,
    classDiagramSVG := svgTask(Def.task(_)).evaluated,
    classDiagramWrite := writeSVGTask.evaluated
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

