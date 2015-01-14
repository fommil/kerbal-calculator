import com.github.fommil.kerbal._

object Solve extends App {
  import Engines.Stock
  import FuelTanks.Stock

  args.toList match {
    case dv :: mass :: a :: atm :: Nil =>
      val solns = Solver.solve(dv.toDouble, mass.toDouble, a.toDouble, atm.toBoolean)
      solns.sortBy(_.initialMass).foreach { soln =>
        println(soln.prettyPrint)
      }

    case _ => println("args: dv mass a")
  }

}
