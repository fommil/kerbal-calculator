package com.github.fommil.kerbal

object Kerbin {
  /** Surface Gravity, m/s^2 */
  val g = 9.81
}

sealed trait Fuel
case object Liquid extends Fuel
case object Solid extends Fuel
case object Mono extends Fuel
case object Xenon extends Fuel

sealed trait Mount
case object Radial extends Mount
case object Tiny extends Mount
case object Small extends Mount
case object Large extends Mount
case object ExtraLarge extends Mount

/**
 * @param name human readable
 * @param mount kind of mounting
 * @param baseCost for the engine with no fuel (Funds)
 * @param baseMass for the engine with no fuel (kKg)
 * @param thrust (kN)
 * @param ispAtm atmosphere Kerbin specific impulse (seconds)
 * @param ispVac vacuum Kerbin specific impulse (seconds)
 * @param fuel kind of fuel to use
 * @loaded fullMass for engine with a full load of internal fuel (kKg)
 * @loaded fullCost for engine with a full load of internal fuel (Funds)
 */
case class Engine(
    name: String,
    mount: Mount,
    baseCost: Double,
    baseMass: Double,
    thrust: Double,
    ispAtm: Double,
    ispVac: Double,
    fuel: Fuel,
    // TODO: refactor internal fuel as a fuel provider
    fullMass: Double = 0,
    fullCost: Double = Double.NaN
) {
  /** Effective exhaust velocity (atmosphere). */
  def veAtm: Double = Kerbin.g * ispAtm
  /** Effective exhaust velocity (vacuum). */
  def veVac: Double = Kerbin.g * ispVac

  /**
   * Total cost with internal fuel accounted for
   * @param loaded fuel to load (kKg)
   */
  def cost(loaded: Double) = {
    val fuelInternal = fullMass - baseMass
    val fuelCost = fullCost - baseCost
    require(loaded <= 0 && loaded <= fuelInternal)
    if (loaded == 0) baseCost
    else baseCost + fuelCost * loaded / fuelInternal
  }
}

object Engines {
  // TODO: parse the engines from a game install (with mods)

  // as of 2015-01-11
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Engines
  val Stock = List(
    // Liquid Engines
    Engine("LV-1R", Radial, 650, 0.03, 4, 220, 290, Liquid),
    Engine("O-10", Radial, 800, 0.09, 20, 220, 290, Mono),
    Engine("Rockomax 24-77", Radial, 480, 0.09, 20, 250, 300, Liquid),
    Engine("Rockomax Mark 55", Radial, 800, 0.9, 120, 320, 360, Liquid),
    Engine("LV-1", Tiny, 350, 0.03, 4, 220, 290, Liquid),
    Engine("Rockomax 48-7S", Tiny, 300, 0.1, 30, 300, 350, Liquid),
    Engine("LV-909", Small, 750, 0.5, 50, 300, 390, Liquid),
    Engine("LV-T30", Small, 850, 1.25, 215, 320, 370, Liquid),
    Engine("LV-T45", Small, 950, 1.5, 200, 320, 370, Liquid),
    Engine("R.A.P.I.E.R. (Liquid)", Small, 3600, 1.2, 175, 320, 360, Liquid),
    Engine("Toroidal Aerospike", Small, 3850, 1.5, 175, 388, 390, Liquid),
    Engine("LV-N Atomic", Small, 8700, 2.25, 60, 220, 800, Liquid),
    Engine("Rockomax \"Poodle\"", Large, 1600, 2, 220, 270, 390, Liquid),
    Engine("Rockomax \"Skipper\"", Large, 2850, 3, 650, 320, 370, Liquid),
    Engine("Rockomax \"Mainsail\"", Large, 5650, 6, 1500, 320, 360, Liquid),
    Engine("LFB KR-1x2", Large, 13462.4, 10, 2000, 290, 340, Liquid, 42, 16400),
    Engine("Kerbodyne KR-2L", ExtraLarge, 20850, 6.5, 2500, 280, 380, Liquid),
    Engine("S3 KS-25x4 Cluster", ExtraLarge, 32400, 9.75, 3200, 320, 360, Liquid),

    // TODO: jet engines

    // Solid Boosters
    Engine("Launch Escape System", Small, 791, 1, 750, 320, 360, Solid, 1.1125, 800),
    Engine("RT-10", Small, 65.2, 0.5, 250, 225, 240, Solid, 3.7475, 325),
    Engine("Rockomax BACC", Small, 190, 1.5, 315, 230, 250, Solid, 7.875, 700),
    Engine("S1 SRB-KD25k", Small, 300, 3.0, 650, 230, 250, Solid, 21.75, 1800),
    Engine("Sepratron I", Small, 45.2, 0.0125, 18, 100, 100, Solid, 0.0725, 50),

    // Ion Engines
    // (doesn't account for electricity supply)
    Engine("PB-ION", Tiny, 5700, 0.25, 2, 0, 4200, Xenon)
  )
}

object CalculateFuel {
  /**
   * Given `(minimum delta v, payload mass, minimum acceleration)` will
   * iterate all engines, filtering on acceleration capability, and
   * return the valid options.
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
    engines: Seq[Engine]
  ): Unit = ???

}
