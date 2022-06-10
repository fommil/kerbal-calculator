import com.github.fommil.kerbal._

object Solve extends App {

  def prettyPrint(s: EngineSolution): String =
    (if (s.numberOfEngines != 1) s"${s.numberOfEngines} x " else "") +
      s"${s.engine.name} with " +
      (if (s.fuelMass == s.tank.max) "Full" else f"${s.fuelMass}%.1ft (${100 * s.fuelMass / s.tank.max}%.0f%%)") +
      f" in a ${s.tank.name}" +
      (if (s.adapters.isEmpty) "" else f" with adapters ${s.adapters.map(_.name)}") +
      f" [a = ${s.initialAccel}%.1f, dv = ${s.totalDeltaV}%.0f, cost = ${s.stageCost}%.0f, mass = ${s.stageInitialMass}%.1ft]"

  implicit val engines: Engines = Engines.Stock
  implicit val tanks: FuelTanks = FuelTanks.Stock
  implicit val adapters: Adapters = Adapters.Stock

  val solns = Solver.solve(args.toList)
  solns.sortBy(_.stageInitialMass).foreach { soln =>
    println(prettyPrint(soln))
  }
}
