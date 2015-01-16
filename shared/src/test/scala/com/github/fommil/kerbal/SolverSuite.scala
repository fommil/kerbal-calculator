package com.github.fommil.kerbal

import utest._

object SolverSuite extends utest.TestSuite {
  def tests = TestSuite {
    "should recommend from two engines for a 10t payload from Kerbin to the Mun"-{
      val results = Solver.solve(1200, 10, 20, false, Large).filter{soln =>
        soln.stageInitialMass < 10
      }
      val got = results.map(_.engine.name).toSet
      val expect = Set("Rockomax \"Skipper\"")
      assert(got == expect)
    }
  }
}
