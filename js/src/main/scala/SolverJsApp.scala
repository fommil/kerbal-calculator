import scala.scalajs.js.JSApp
import scalatags.Text.all._
import org.scalajs.jquery.jQuery
import org.scalajs.dom

import com.github.fommil.kerbal._

object SolverJsApp extends JSApp {

  // FIXME: cookies are Work In Progress
  // note that you need to run from a webserver as some browsers
  // will not store cookies for filesystem HTMLs.

  def cookies = dom.document.cookie.split("; ").map{ entry =>
    val i = entry.indexOf("=")
    (entry.substring(0, i), entry.substring(i, entry.length))
  }.toMap

  def addCookie(key: String, value: String): Unit = {
    dom.document.cookie = (cookies + (key -> value)).map{
      case (k,v) => s"$k=$v"
    }.mkString("; ")
  }

  def setupUI(): Unit = {
    jQuery("#submit").click(solve _)

    val sizes = jQuery("#size")
    val saved = cookies.get("size").getOrElse("Large")

    implicitly[Adapters].adapters.map(_.upper).distinct.map {
      case mount if mount.name == saved =>
        // bit of an ugly hack
        option(mount.name, "selected".attr := "")
      case mount => option(mount.name)
    }.foreach { entry =>
      sizes.append(entry.render)
    }

    List("dv", "M", "a").foreach { id =>
      cookies.get(id).foreach { v =>
        setParam(id, v)
      }
    }
  }

  def main(): Unit = {
    jQuery(setupUI _)
  }

  private def getParam(id: String) = jQuery("#" + id).value().toString
  private def getCheckbox(id: String) = jQuery("#" + id).is(":checked")

  private def setParam(id: String, value: String) = jQuery("#" + id).attr("value", value)
//  private def setCheckbox(id: String, value: ) = jQuery("#" + id).is(":checked")

  def solve(): Unit = {
    jQuery("#spinner").show()
    jQuery("#results").empty()

    try {
      val dv = getParam("dv").toDouble
      val m = getParam("M").toDouble
      val a = getParam("a").toDouble
      val atm = getCheckbox("atm")
      val mount = Mount.fromName(getParam("size"))

      val solns = Solver.solve(dv, m, a, atm, mount)
      jQuery("#results").append(tabulate(solns.sortBy(_.stageInitialMass)))

      addCookie("dv", dv.toString)
      addCookie("M", m.toString)
      addCookie("a", a.toString)
      addCookie("atm", atm.toString)
      addCookie("size", mount.name)
    } finally {
      jQuery("#spinner").hide()
    }

  }

  def tabulate(solns: Seq[EngineSolution]) = table(`class` := "table table-hover")(
    thead(
      tr(
        th("Engine"), th("Tank"), th("Fuel"), th("Adapters"), th("Δv"), th("a"), th("M"), th("cost")
      )
    ),
    tbody(
      solns.zipWithIndex.map {
        case (s, i) =>
          tr(`class` := (if (i == 0) "success" else ""))(
            td(s"${s.numberOfEngines} x ${s.engine.name}"),
            td(s.tank.name),
            td(f"${s.fuelMass}%.3ft (${100 * s.fuelMass / s.tank.max}%.0f%%)"),
            td(s.adapters.map(_.name).mkString(", ")),
            td(f"${s.totalDeltaV}%.0f"),
            td(f"${s.initialAccel}%.1f"),
            td(f"${s.stageInitialMass}%.1f"),
            td(f"${s.stageCost}%.0f")
          )
      }
    )
  ).render
}
