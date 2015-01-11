package com.github.fommil.kerbal

import org.scalatest._

class EngineSpec extends FunSpec with Matchers {
  import Engines.Stock
  import FuelTanks.Stock

  val poodle = Engines.Stock.engines.find(_.name.contains("Poodle")).get
  val kr1x2 = Engines.Stock.engines.find(_.name.contains("KR-1x2")).get
  val largeTanks = FuelTanks.Stock.tanks.filter(_.mount == Large)
  require(largeTanks.nonEmpty)

//  describe ("com.github.fommil.kerbal.Engine") {
    it("should correctly return valid fuel tanks") {
      assert(poodle.validTanks === largeTanks)
      assert(kr1x2.validTanks === largeTanks)
    }
//  }
}
