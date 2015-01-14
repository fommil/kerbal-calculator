
import com.github.fommil.kerbal.EngineSolution
import scala.scalajs.js.JSApp
import scalatags.Text.all._
import org.scalajs.jquery.jQuery

import com.github.fommil.kerbal.Solver

object SolverJsApp extends JSApp {
  def setupUI(): Unit = {
    jQuery("#submit").click(solve _)
  }

  def main(): Unit = {
    jQuery(setupUI _)
  }

  private def getParam(id: String) = jQuery("#" + id).value().toString
  private def getCheckbox(id: String) = jQuery("#" + id).is(":checked")

  def solve(): Unit = {
    jQuery("#spinner").show()

    try {
      val dv = getParam("dv").toDouble
      val m = getParam("M").toDouble
      val a = getParam("a").toDouble
      val atm = getCheckbox("atm")

      val solns = Solver.solve(dv, m, a, atm)

      //jQuery("#results").append(solns.sortBy(_.initialMass).map(_.prettyPrint).mkString("<br/>"))
      jQuery("#results").replaceWith(tabulate(solns.sortBy(_.initialMass)))

    } finally {
      jQuery("#spinner").hide()
    }

  }

  def tabulate(solns: Seq[EngineSolution]) = table(`class`:="table table-hover")(
    thead(
      tr(
        th("Engine"), th("Fuel"), th("Tank"), th("Δv"), th("a"), th("M"), th("cost")
      )
    ),
    tbody(
      solns.zipWithIndex.map { case (s, i) =>
        tr(`class`:= (if(i == 0) "success" else ""))(
          td(s"${s.numberOfEngines} x ${s.engine.name}"),
          td(f"${s.fuelMass}%.3ft (${100 * s.fuelMass / s.tank.max}%.0f%%)"),
          td(s.tank.name),
          td(f"${s.totalDeltaV}%.0f"),
          td(f"${s.initialAccel}%.1f"),
          td(f"${s.initialMass}%.1f"),
          td(f"${s.stageCost}%.0f")
        )
      }
    )
  ).render
}
