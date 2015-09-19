package diagram

import Reflect.getAllClassAndTrait

import scala.reflect.NameTransformer

final case class ClassNode(clazz: Class[_], parents: List[Class[_]]) {
  lazy val allParents = getAllClassAndTrait(this.clazz)
}

object ClassNode {

  def decodeClassName(name: String): String =
    name.split('.').map(NameTransformer.decode).mkString(".").replace("\\", "\\\\")

  def dot(allClassNodes: List[ClassNode], setting: DiagramSetting): String = {
    val quote = "\"" + (_: String) + "\""
    val map2string = { (map: Map[String, String]) =>
      if(map.isEmpty) ""
      else map.map { case (k, v) => k + "=" + quote(v)}.mkString(" [", ", ", "]")
    }

    val nodes = allClassNodes.map{ n =>
      quote(setting.nodeName(n.clazz)) + map2string(setting.nodeSetting(n.clazz))
    }.sorted
    val edges = for {
      c <- allClassNodes
      p <- c.parents
      if setting.filter(p)
    } yield {
      quote(setting.nodeName(c.clazz)) + " -> " + quote(setting.nodeName(p)) + map2string(setting.edgeSetting(c.clazz, p))
    }

    s"""digraph ${quote(setting.name)} {
    |
    |  node ${map2string(setting.commonNodeSetting)}
    |
    |  edge ${map2string(setting.commonEdgeSetting)}
    |
    |  ${nodes.mkString("\n")}
    |
    |  ${edges.mkString("\n")}
    |
    | }""".stripMargin
  }
}
