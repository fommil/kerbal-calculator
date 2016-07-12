package com.github.fommil.kerbal

import utest._

object SolverSuite extends utest.TestSuite {

  private def bestEngines(solns: Stream[EngineSolution]): Set[String] =
    solns.sortBy(_.stageInitialMass).take(20).map(_.engine.name).toSet

  private def similar(a: Double, b: Double, e: Double = 1e-06): Boolean = (a >= (b - e)) && (a <= (b + e))

  def tests = TestSuite {
    "should calculate the correct deltav for a mainsail with orange tank" - {
      // http://forum.kerbalspaceprogram.com/threads/107592-web-calculator-for-engines-fuel
      val mainsail = Engines.Stock.engines.find(_.name.contains("Mainsail")).get
      val orange = FuelTanks.Stock.tanks.find(_.name.contains("Jumbo-64")).get
      val dv = EngineSolution(11, mainsail, 1, orange, 32, false, Nil).totalDeltaV

      assert(similar(dv, 2815, 1.0))
    }

    "should recommend sensible engines for a 10t payload from Kerbin to the Mun" - {
      val results = Solver.solve(1200, 10, 20, false, Large)
      val engines = bestEngines(results)
      val expect = Set(
        "Skipper",
        "Thud",
        "Thumper (Radial)",
        "Kickback"
      )
      assert(engines == expect)
    }

    "should recommend sensible engines for a long-burn small satellite" - {
      val results = Solver.solve(1000, 1, 1, false, Tiny)
      val engines = bestEngines(results)
      val expect = Set(
        "Dawn",
        "Spider"
      )
      assert(engines == expect)
    }

    "should recommend radial solid boosters and lifters for a lift-off" - {
      val results = Solver.solve(1000, 50, 10, true, Large)
      val engines = bestEngines(results)
      val expect = Set(
        "Mainsail",
        "Twin-Boar",
        "Mammoth",
        "Rhino"
      )
      assert(engines == expect)
    }
  }
}
