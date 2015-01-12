package com.github.fommil.kerbal

import scala.scalajs.test.JasmineTest

object EngineSpec extends JasmineTest {
  import Engines.Stock
  import FuelTanks.Stock

  val poodle = Engines.Stock.engines.find(_.name.contains("Poodle")).get
  val kr1x2 = Engines.Stock.engines.find(_.name.contains("KR-1x2")).get

  val largeLiquidTanks = FuelTanks.Stock.tanks.filter { tank =>
    tank.mount == Large && tank.fuel == Liquid
  }.toSet

  describe("Engine") {
    it("should return valid fuel tanks for the Poodle engine") {
      assert(poodle.validTanks == largeLiquidTanks)
    }

    it("should return valid fuel tanks for the KR-1x2 engine") {
      // it has an internal engine, but that is obtained separately
      assert(kr1x2.validTanks == largeLiquidTanks)
    }
  }
}
