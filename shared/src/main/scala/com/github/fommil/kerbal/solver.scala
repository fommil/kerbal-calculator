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
  engine: Engine,
  numberOfEngines: Int,
  tank: FuelTank,
  fuelMass: Double
) {
  require(fuelMass > 0 && fuelMass <= tank.max)

  // of the engine stage (i.e. not including the payload)
  def stageCost: Double = engine.cost + tank.cost(fuelMass)
  def initialMass: Double = engine.mass + tank.mass(fuelMass)
  def finalMass: Double = engine.mass + tank.mass(0)

  // F = Ma
  def initialAccel: Double = engine.thrust / (initialMass + payloadMass)

  // \Delta v = v_e * ln (m_0 / m_1)
  def totalDeltaV: Double =
    engine.veVac * numberOfEngines * log((payloadMass + initialMass) / (payloadMass + finalMass))

  def prettyPrint: String =
    (if (numberOfEngines != 1) s"$numberOfEngines x " else "") +
      s"${engine.name} with " +
      (if (fuelMass == tank.max) "Full" else f"${fuelMass}%.1ft (${100 * fuelMass / tank.max}%.0f%%)") +
  f" in a ${tank.name}" +
  f" [a = ${initialAccel}%.1f, dv = ${totalDeltaV}%.0f, cost = ${stageCost}%.0f, mass = ${initialMass}%.1ft]"

}

object Solver {
  /**
   * Given `(minimum delta v, payload mass, minimum acceleration)`
   * will iterate all engines and fuel tanks, filtering on
   * acceleration capability, and return the valid options.
   *
   * TODO: trim the results so that every filling of the tank is not
   * shown. e.g. currently if a tank works with [50, 100%] of fuel,
   * [50.0, 50.1, 50.2...] will all be provided as valid answers. Or
   * keep this to plot graphically.
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
   * @param safety margin to use for upper limits
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
    tank <- engine.validTanks ++ engine.internal
    fuel <- (0 to 100 by 5).map(_ / 100.0 * tank.max)
    if fuel > 0
  } yield EngineSolution(payloadMass, engine, numEngines, tank, fuel)

}
