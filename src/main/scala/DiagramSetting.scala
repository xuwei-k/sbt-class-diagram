package diagram

final case class DiagramSetting(
  name: String,
  commonNodeSetting: Map[String, String],
  commonEdgeSetting: Map[String, String],
  nodeName: Class[_] => String,
  nodeSetting: Class[_] => Map[String, String],
  edgeSetting: (Class[_], Class[_]) => Map[String, String],
  filter: Class[_] => Boolean
)
