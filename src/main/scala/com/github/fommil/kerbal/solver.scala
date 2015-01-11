package com.github.fommil.kerbal

import math.log

/**
 * @param payloadMass (kKg)
 * @param engines
 * @param tank to use
 * @param mass of fuel (kKg)
 */
case class EngineSolution(
  payloadMass: Double,
  engines: List[Engine],
  tank: FuelTank,
  fuelMass: Double
) {
  // of the engine stage (i.e. not including the payload)
  lazy val totalCost: Double = engines.map(_.cost).sum + tank.cost(fuelMass)
  lazy val initialMass: Double = engines.map(_.mass).sum + tank.mass(fuelMass)
  lazy val finalMass: Double = engines.map(_.mass).sum + tank.mass(0)

  // F = Ma
  lazy val initialAccel: Double = engines.map(_.thrust).sum / initialMass

  // \Delta v = v_e * ln (m_0 / m_1)
  // I_{sp} = \frac{\sum\limits_i F_{T_i} }
  //               {\sum\limits_i \frac{F_{T_i} }{I_{sp_i} } }
  lazy val totalDeltaV: Double = {
    val thrust = engines.map(_.thrust).sum
    val partImpulses = engines.map{e => e.thrust / e.ispVac}.sum
    val ve = Kerbin.g * thrust / partImpulses
    ve * log(finalMass / initialMass)
  }
}

object Solver {
  /**
   * Given `(minimum delta v, payload mass, minimum acceleration)`
   * will iterate all engines and fuel tanks, filtering on
   * acceleration capability, and return the valid options.
   *
   * Things not currently supported:
   *
   * 0. atmospheric use.
   * 1. multiple stages (although, this is supported by
   *    reducing your dv requirement and calling this recursively
   *    after adding the additional decoupler mass cost).
   * 2. multiple fuel tanks (e.g. won't consider radially-mounted
   *    engines that each need a separate fuel tank, or adding
   *    additional fuel to an engine with an internal tank)
   * 3. mixing and matching mount sizes (e.g. Large tank with
   *    two/three/four Small engines)
   * 4. electricity / reaction wheel requirements
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
  ): Stream[EngineSolution] = for {
    engine <- engines.engines.toStream
    candidate <- candidates(engine, payloadMass)
    if candidate.totalDeltaV >= dvMin
    if candidate.initialAccel >= accelMin
  } yield candidate

  private def candidates(
    engine: Engine, payloadMass: Double
  )(implicit allTanks: FuelTanks): Stream[EngineSolution] = for {
    numEngines <- (1 to 8).toStream // consider up to 8 radial engines
    if numEngines == 1 | engine.mount == Radial
    tank <- engine.validTanks
    fuel <- (0.0 to tank.max by 0.1)
    if fuel > 0 & fuel <= tank.max // to, by, can jump the boundary
  } yield EngineSolution(payloadMass, List.fill(numEngines)(engine), tank, fuel)

}
