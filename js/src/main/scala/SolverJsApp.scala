
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

  def solve(): Unit = {
    jQuery("#spinner").show()

    try {
      val dv = getParam("dv").toDouble
      val m = getParam("M").toDouble
      val a = getParam("a").toDouble

      println(s"$dv $m $a")

      val solns = Solver.solve(dv, m, a)

      jQuery("#results").append(solns.sortBy(_.initialMass).map(_.prettyPrint).mkString("<br/>"))

    } finally {
      jQuery("#spinner").hide()
    }

  }

  // TODO: use this example to output a nice table rendering of the solution
   // val listing =
   //      table(
   //        caption("Stories"),
   //        thead(
   //          tr(
   //            th("Title"), th("Tags"), th("Chapters"), th("Created"), th("Last modified")
   //          )
   //        ),
   //        tfoot(
   //          tr(
   //            th("Title"), th("Tags"), th("Chapters"), th("Created"), th("Last modified")
   //          )
   //        ),
   //        tbody(
   //          "blah"
   //        )
   //  ).render
}
