package com.github.fommil.kerbal

import org.scalatest._

class EngineTest extends WordSpec {
  val poodle = Engines.Stock.engines.find(_.name.contains("Poodle")).get
  val kr1x2 = Engines.Stock.engines.find(_.name.contains("Twin-Boar")).get

  val largeLiquidTanks = FuelTanks.Stock.tanks.filter { tank =>
    tank.mount == Large && tank.fuel == Liquid
  }.toSet

  "Engines" should {
    "return valid fuel tanks for the Poodle engine" in {
      assert(poodle.validTanks == largeLiquidTanks)
    }

    "return valid fuel tanks for the KR-1x2 engine" in {
      // it has an internal engine, but that is obtained separately
      assert(kr1x2.validTanks == largeLiquidTanks)
    }
  }

}
