package com.github.fommil.kerbal

import utest._
import utest.ExecutionContext.RunNow

object SolverSpec extends TestSuite {
  import Engines.Stock
  import FuelTanks.Stock

  val tests = TestSuite {
    "should solve for a Kerbin to Mun 10 payload"-{
      val results = Solver.solve(1200, 10, 20)
      assert(results.nonEmpty)
      println(results.sortBy(_.initialMass).take(3).map(_.prettyPrint).mkString("\n"))

      // FIXME: should not contain Launch Escape System
      println(results.sortBy(_.totalCost).take(3).map(_.prettyPrint).mkString("\n"))
    }

    // TODO: test with two types of engines, one more efficient than the other, it should always be selected
  }
}