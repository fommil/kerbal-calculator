package com.github.fommil.kerbal

import utest._
import utest.ExecutionContext.RunNow

object EngineSuite extends TestSuite {
  import Engines.Stock
  import FuelTanks.Stock

  val poodle = Engines.Stock.engines.find(_.name.contains("Poodle")).get
  val kr1x2 = Engines.Stock.engines.find(_.name.contains("KR-1x2")).get

  val largeLiquidTanks = FuelTanks.Stock.tanks.filter { tank =>
    tank.mount == Large && tank.fuel == Liquid
  }.toSet

  val tests = TestSuite {
    "should return valid fuel tanks for the Poodle engine" - {
      assert(poodle.validTanks == largeLiquidTanks)
    }

    "should return valid fuel tanks for the KR-1x2 engine" - {
      // it has an internal engine, but that is obtained separately
      assert(kr1x2.validTanks == largeLiquidTanks)
    }
  }
}
