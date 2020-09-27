package zinteract

sealed trait ZinteractError extends Exception
case class FailLinkError(url: String) extends ZinteractError {
  override def getMessage(): String = {
    s"Zinteract can't reach the following url: $url"
  }
}
