package com.github.fommil.kerbal

import org.scalatest.FunSpec
import org.scalatest.Matchers

class SolverSpec extends FunSpec with Matchers {
  import Engines.Stock
  import FuelTanks.Stock

  describe ("com.github.fommil.kerbal.Solver") {
    it("should do something") {
      val results = Solver.solve(100, 10, 1)
      assert(results.nonEmpty)
      println(results)
    }
  }
}
