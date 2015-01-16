import com.github.fommil.kerbal._

object Solve extends App {
  args.toList match {
    case dv :: mass :: a :: atm :: size :: Nil =>
      val solns = Solver.solve(
        dv.toDouble,
        mass.toDouble,
        a.toDouble,
        atm.toBoolean,
        Mount.fromName(size)
      )
      solns.sortBy(_.stageInitialMass).foreach { soln =>
        println(soln.prettyPrint)
      }

    case _ => println("args: dv mass a")
  }

}
