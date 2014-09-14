package diagram

import Reflect.getAllClassAndTrait

final case class ClassNode(clazz: Class[_], parents: List[Class[_]]) {
  import ClassNode._

  lazy val allParents = getAllClassAndTrait(this.clazz)

  private lazy val fullName = clazz.getName
}

object ClassNode {

  def dot(allClassNodes: List[ClassNode]): String = {
    val header = """digraph "class-diagram" {
      |    node [
      |        shape="record"
      |    ]
      |    edge [
      |        arrowtail="none"
      |    ]""".stripMargin

    val quote = "\"" + (_: String) + "\""

    val edges = allClassNodes.map(n => quote(n.fullName)).sorted.mkString("\n")
    val nodes = for {
      c <- allClassNodes
      p <- c.parents
    } yield {
      quote(c.fullName) + " -> " + quote(p.getName)
    }

    List(header, edges, nodes.sorted.mkString("\n")).mkString("\n") + "}"
  }
}
