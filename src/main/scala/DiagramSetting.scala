package diagram

final case class DiagramSetting(
  name: String,
  commonNodeSetting: Map[String, String],
  commonEdgeSetting: Map[String, String],
  nodeSetting: Class[?] => Map[String, String],
  edgeSetting: (Class[?], Class[?]) => Map[String, String],
  filter: Class[?] => Boolean
)
