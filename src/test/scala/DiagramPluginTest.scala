package diagram

import org.junit.Assert.assertEquals
import org.junit.Test
import sbt.complete.Parser
import scala.util.Random

final class DiagramPluginTest {

  private[this] def invokeParser(classes: Seq[String]): Parser[Seq[String]] = {
    val clazz = diagram.ClassDiagramPlugin.getClass
    val obj = clazz.getField("MODULE$").get(null)
    val methodName = "createParser"
    val methods = clazz.getDeclaredMethods
    def methodNames = methods.map(_.getName).toList
    val method = methods.find(_.getName endsWith methodName).getOrElse(sys.error("not found " + methodName + " in " + methodNames))
    method.setAccessible(true)
    method.invoke(obj, classes).asInstanceOf[Parser[Seq[String]]]
  }

  @Test
  def tabCompletions(): Unit = {
    val classes = List("A", "B", "C").map("com.example." + _)
    val h :: t = Random.shuffle(classes)
    val parser = invokeParser(classes)
    val result = (" " + h + " ").foldLeft(parser){_ derive _}.completions(0).get.map(_.display)
    assertEquals(result, t.toSet)
  }

}
