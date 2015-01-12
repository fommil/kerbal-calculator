package com.github.fommil.kerbal

import org.scalatest.FunSpec
import org.scalatest.Matchers

class SolverSpec extends FunSpec with Matchers {
  import Engines.Stock
  import FuelTanks.Stock

  describe ("com.github.fommil.kerbal.Solver") {
    it("should do something") {
      val results = Solver.solve(1200, 10, 20)
      assert(results.nonEmpty)
      println(results.sortBy(_.initialMass).take(3).map(_.prettyPrint).mkString("\n"))

      // FIXME: should not contain Launch Escape System
      println(results.sortBy(_.totalCost).take(3).map(_.prettyPrint).mkString("\n"))
    }

    // TODO: test with two types of engines, one more efficient than the other, it should always be selected
  }
}
