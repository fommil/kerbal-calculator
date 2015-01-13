import scala.scalajs.js.JSApp
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport

object SolverJsApp extends JSApp {
  def setupUI(): Unit = {
    jQuery("#click-me-button").click(addClickedMessage _)
    jQuery("body").append("<p>Hello World</p>")
  }

  def main(): Unit = {
    jQuery(setupUI _)
  }

  def addClickedMessage(): Unit = {
    jQuery("body").append("<p>You Clicked</p>")
  }
}
