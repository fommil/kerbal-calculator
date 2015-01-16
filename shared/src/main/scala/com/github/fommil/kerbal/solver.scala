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
  fuelMass: Double,
  atmosphere: Boolean,
  adapters: List[Adapter]
) {
  require(fuelMass > 0 && fuelMass <= tank.max)
  require(adapters.size <= 2)

  // of the engine stage (i.e. not including the payload)
  def stageCost: Double = numberOfEngines * engine.cost + tank.cost(fuelMass) + adapters.map(_.cost).sum
  def stageInitialMass: Double = numberOfEngines * engine.mass + tank.mass(fuelMass) + adapters.map(_.mass).sum
  def stageFinalMass: Double = numberOfEngines * engine.mass + tank.mass(0) + adapters.map(_.mass).sum

  // F = Ma
  def initialAccel: Double = numberOfEngines * engine.thrust / (stageInitialMass + payloadMass)

  // \Delta v = v_e * ln (m_0 / m_1)
  def totalDeltaV: Double = {
    val ve = if (atmosphere) engine.veAtm else engine.veVac
    ve * numberOfEngines * log((payloadMass + stageInitialMass) / (payloadMass + stageFinalMass))
  }

  def prettyPrint: String =
    (if (numberOfEngines != 1) s"$numberOfEngines x " else "") +
      s"${engine.name} with " +
      (if (fuelMass == tank.max) "Full" else f"${fuelMass}%.1ft (${100 * fuelMass / tank.max}%.0f%%)") +
      f" in a ${tank.name}" +
      (if (adapters.isEmpty) "" else f" with adapters ${adapters.map(_.name)}") +
      f" [a = ${initialAccel}%.1f, dv = ${totalDeltaV}%.0f, cost = ${stageCost}%.0f, mass = ${stageInitialMass}%.1ft]"

}

object Solver {
  /**
   * Given `(minimum delta v, payload mass, minimum acceleration)`
   * will iterate all engines and fuel tanks, filtering on
   * acceleration capability, and return the valid options.
   *
   * Things not currently supported:
   *
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
   * @param atmosphere is the manoeuvre to be performed in atmosphere
   * @param size of the payload mount
   */
  def solve(
    dvMin: Double,
    payloadMass: Double,
    accelMin: Double,
    atmosphere: Boolean,
    size: Mount
  )(
    implicit
    engines: Engines,
    allTanks: FuelTanks,
    adapters: Adapters
  ): Stream[EngineSolution] = for {
    engine <- engines.engines.toStream
    candidate <- candidates(engine, payloadMass, atmosphere, size)
    dv = candidate.totalDeltaV
    if dv >= dvMin & dv < dvMin * 2 // arbitrary cutoff
    a = candidate.initialAccel
    if a >= accelMin & a < accelMin * 2 + 10 // arbitrary cutoff
  } yield candidate

  private def candidates(
    engine: Engine, payloadMass: Double, atmosphere: Boolean, size: Mount
  )(implicit allTanks: FuelTanks, allAdapters: Adapters): Stream[EngineSolution] = for {
    tank <- (engine.validTanks ++ engine.internal).toStream
    (payloadAdapter, engineAdapter) <- adapt(size, engine, tank)
    numEngines <- (1 to engine.mount.max(tank))
    fuel <- (0 to 100 by 5).map(_ / 100.0 * tank.max)
    if fuel > 0
    adapters = (payloadAdapter ++ engineAdapter).toList
  } yield EngineSolution(payloadMass, engine, numEngines, tank, fuel, atmosphere, adapters)

  private def adapt(payload: Mount, engine: Engine, tank: FuelTank)(implicit all: Adapters): List[(Option[Adapter], Option[Adapter])] = {
    def valids(upper: Mount, lower: Mount): List[Option[Adapter]] =
      if (upper == lower) List(None)
      else for {
        adapter <- all.adapters
        if adapter.lower == lower & adapter.upper == upper
      } yield Some(adapter)
    for {
      top <- valids(payload, tank.mount)
      bottom <- valids(tank.mount, engine.mount)
    } yield (top, bottom)
  }
}
