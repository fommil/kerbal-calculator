import scala.scalajs.js.JSApp
import scalatags.Text.all._
import org.scalajs.jquery.jQuery
import org.scalajs.dom

import com.github.fommil
import fommil.js._
import fommil.kerbal._

object SolverJsApp extends JSApp
  with CookieSupport
  with InputFormSupport
  with PersistentInputFormSupport {

  // ids of the input parameters
  val ids = List("dv", "M", "a", "atm", "size")

  def main(): Unit = {
    jQuery("#submit").click(solve _)

    val sizes = implicitly[Adapters].adapters.map(_.upper.name).distinct
    populate("size", sizes)

    loadParams(ids)
  }

  def solve(): Unit = {
    jQuery("#spinner").show()
    jQuery("#results").empty()

    try {
      val params = getParams(ids)
      println(params)

      val solns = Solver.solve(ids.map(params(_)))

      jQuery("#results").append(tabulate(solns.sortBy(_.stageInitialMass)))

      persistParams(params)
    } finally {
      jQuery("#spinner").hide()
    }
  }

  private def optLink(name: String, wikiName: Option[String]) =
    wikiName.map { wiki =>
      a(
        target := "_blank",
        href := "http://wiki.kerbalspaceprogram.com/wiki/" + wiki
      )(name)
    }.getOrElse(name)

  private def tabulate(solns: Seq[EngineSolution]) = table(`class` := "table table-hover")(
    thead(
      tr(
        th("Engine"), th("Tank"), th("Fuel"), th("Adapters"), th("Δv"), th("a"), th("M"), th("cost")
      )
    ),
    tbody(
      solns.zipWithIndex.map {
        case (s, i) =>
          tr(`class` := (if (i == 0) "success" else ""))(
            raw(
              "<td>" +
                (if (s.numberOfEngines == 1) "" else s"${s.numberOfEngines} x ") +
                optLink(s.engine.name, s.engine.wiki) +
                "</td>"
            ),
            raw(
              "<td>" +
                optLink(s.tank.name, s.tank.wiki) +
                "</td>"
            ),
            td(f"${s.fuelMass}%.3ft (${100 * s.fuelMass / s.tank.max}%.0f%%)"),
            raw(
              "<td>" + {
                if (s.adapters.isEmpty) "-"
                else {
                  s.adapters.map { a => optLink(a.name, a.wiki) }.mkString(";")
                }
              } + "</td>"
            ),
            td(f"${s.totalDeltaV}%.0f"),
            td(f"${s.initialAccel}%.1f"),
            td(f"${s.stageInitialMass}%.1f"),
            td(f"${s.stageCost}%.0f")
          )
      }
    )
  ).render
}
