package diagram

import Reflect.getAllClassAndTrait

import scala.reflect.NameTransformer

final case class ClassNode(clazz: Class[_], parents: List[Class[_]]) {
  lazy val allParents = getAllClassAndTrait(this.clazz)

  private lazy val fullName = clazz.getName
  private def fullNameDecoded = ClassNode.decodeClassName(fullName).replace("\\", "\\\\")
}

object ClassNode {

  def decodeClassName(name: String): String =
    name.split('.').map(NameTransformer.decode).mkString(".")

  def dot(allClassNodes: List[ClassNode], setting: DiagramSetting): String = {
    val quote = "\"" + (_: String) + "\""
    val map2string = { (map: Map[String, String]) =>
      if(map.isEmpty) ""
      else map.map { case (k, v) => k + "=" + quote(v)}.mkString(" [", ", ", "]")
    }

    val nodes = allClassNodes.map{ n =>
      quote(n.fullNameDecoded) + map2string(setting.nodeSetting(n.clazz))
    }.sorted
    val edges = for {
      c <- allClassNodes
      p <- c.parents
      if setting.filter(p)
    } yield {
      quote(c.fullNameDecoded) + " -> " + quote(p.getName) + map2string(setting.edgeSetting(c.clazz, p))
    }

    s"""digraph ${quote(setting.name)} {

node ${map2string(setting.commonNodeSetting)}

edge ${map2string(setting.commonEdgeSetting)}

${nodes.mkString("\n")}

${edges.mkString("\n")}

}"""
  }
}
