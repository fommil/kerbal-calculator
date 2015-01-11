package com.github.fommil.kerbal

/**
 * @param engines including mass of the internal tank, if appropriate
 * @param tank and mass of fuel
 */
case class EngineSolution(
  engines: List[(Engine, Option[Double])],
  tank: (FuelTank, Double)
) {
  lazy val totalDeltaV: Double = ???
  lazy val initialAccel: Double = ???
  lazy val totalCost: Double = ???
  lazy val totalMass: Double = ???
}

object Solver {
  /**
   * Given `(minimum delta v, payload mass, minimum acceleration)`
   * will iterate all engines and fuel tanks, filtering on
   * acceleration capability, and return the valid options.
   *
   * Things not currently supported:
   *
   * 1. multiple stages
   * 2. multiple fuel tanks (except internal engines)
   * 3. electricity / reaction wheel requirements
   *
   * Uses https://en.wikipedia.org/wiki/Tsiolkovsky_rocket_equation
   *
   *   \Delta v = v_e * ln (m_0 / m_1)
   *
   * @param dvMin minimum delta v required to perform the manoeuvres
   * @param payloadMass the mass of the payload to be transported
   * @param accelMin the minimum delta v / second required to manoeuvre
   * @param engines the options to iterate
   */
  def solve(
    dvMin: Double,
    payloadMass: Double,
    accelMin: Double
  )(
    implicit
    engines: Engines,
    allTanks: FuelTanks
  ): Unit = {
    ???
  }

}
