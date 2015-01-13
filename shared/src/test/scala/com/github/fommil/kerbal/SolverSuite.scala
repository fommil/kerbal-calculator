package com.github.fommil.kerbal

import utest._

object SolverSuite extends utest.TestSuite {
  import Engines.Stock
  import FuelTanks.Stock

  def tests = TestSuite {
    "should recommend one engine for a 10t payload from Kerbin to the Mun"-{
      val results = Solver.solve(1200, 10, 20).filter{soln =>
        soln.initialMass < 10
      }
      assert(results.map(_.engine.name).distinct.size == 1)
    }

    // TODO: test with two types of engines, one more efficient than the other, it should always be selected
  }
}
