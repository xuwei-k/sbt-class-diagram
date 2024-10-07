package diagram

@deprecated(message = "will be removed", since = "0.2.2")
private[diagram] object Serialization {
  val Implicits = sjsonnew.BasicJsonProtocol
}
