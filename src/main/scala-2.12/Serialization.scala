package diagram

@deprecated(message = "will be removed", since = "0.3.0")
private[diagram] object Serialization {
  val Implicits = sjsonnew.BasicJsonProtocol
}
