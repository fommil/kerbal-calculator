package com.github.fommil.kerbal

sealed trait Fuel {
  /**
   * Converts a mass, specified in the fuel's natural units,
   * to 't'(onnes), ignoring oxidiser.
   */
  def toTonnes(units: Double): Double
}
case object Liquid extends Fuel {
  def toTonnes(lg: Double) = 0.005 * lg
}
case object Solid extends Fuel {
  def toTonnes(solid: Double) = 0.0075 * solid
}
case object Mono extends Fuel {
  def toTonnes(mono: Double) = 0.004 * mono
}
case object Xenon extends Fuel {
  def toTonnes(xe: Double) = 0.0001 * xe
}

sealed trait FuelTank {
  /** Human readable name */
  def name: String
  /** Kind of fuel */
  def fuel: Fuel
  /** Kind of mounting */
  def mount: Mount
  /** Maximum supplied fuel (kKg) per container */
  def max: Double
  /** returns cost (Funds) for container with requested fuel (kKg) */
  def cost(amount: Double): Double
  /** returns mass (kKg) for container with requested fuel (kKg) */
  def mass(amount: Double): Double
  /** returns true iff a maximum of one tank of this type can be installed */
  def unique: Boolean
  /** name on the kerbal wiki */
  def wiki: Option[String]
}
/**
 * @param baseMass mass without any fuel
 * @param baseCost cost without any fuel
 * @param fullCost cost with a full tank
 * @param unique is only one allowed
 */
case class FixedFuelTank(
  name: String,
  fuel: Fuel,
  mount: Mount,
  baseCost: Double,
  fullCost: Double,
  emptyMass: Double,
  fullMass: Double,
  unique: Boolean = false,
  wiki: Option[String] = None
) extends FuelTank {
  require(fullMass > 0, name)
  require(emptyMass >= 0, name)
  require(fullMass > emptyMass, name)

  def max = fullMass - emptyMass

  def cost(amount: Double): Double = {
    require(amount >= 0 && amount <= max)
    val fuelCost = fullCost - baseCost
    baseCost + fuelCost * amount / max
  }
  def mass(amount: Double): Double = {
    require(amount >= 0 && amount <= max, s"$amount was outside [0, $max]")
    emptyMass + amount
  }
}

class FuelTanks(val tanks: List[FuelTank])
object FuelTanks {
  // as of 2015-01-11
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Fuel_Tanks
  implicit val Stock = new FuelTanks(List(
    // Liquid
    FixedFuelTank("ROUND-8 Toroidal", Liquid, Tiny, 349.8, 360, 0.025, 0.136, wiki = Some("ROUND-8_Toroidal_Fuel_Tank")),
    FixedFuelTank("Oscar-B", Liquid, Tiny, 174.2, 180, 0.015, 0.78675, wiki = Some("Oscar-B_Fuel_Tank")),
    FixedFuelTank("FL-T100", Liquid, Small, 204.1, 250, 0.0625, 0.5625, wiki = Some("FL-T100_Fuel_Tank")),
    FixedFuelTank("FL-T200", Liquid, Small, 333.2, 425, 0.125, 1.125, wiki = Some("FL-T200_Fuel_Tank")),
    FixedFuelTank("FL-T400", Liquid, Small, 666.4, 850, 0.25, 2.25, wiki = Some("FL-T400_Fuel_Tank")),
    FixedFuelTank("FL-T800", Liquid, Small, 1232.8, 1600, 0.5, 4.5, wiki = Some("FL-T800_Fuel_Tank")),
    FixedFuelTank("Rockomax X200-8", Liquid, Large, 1232.8, 1600, 0.5, 4.5, wiki = Some("Rockomax_X200-8_Fuel_Tank")),
    FixedFuelTank("Rockomax X200-16", Liquid, Large, 2465.6, 3200, 1, 9, wiki = Some("Rockomax_X200-16_Fuel_Tank")),
    FixedFuelTank("Rockomax X200-32", Liquid, Large, 4931.2, 6400, 2, 18, wiki = Some("Rockomax_X200-32_Fuel_Tank")),
    FixedFuelTank("Rockomax Jumbo-64", Liquid, Large, 9862.4, 12800, 4, 36, wiki = Some("Rockomax_Jumbo-64_Fuel_Tank")),
    FixedFuelTank("Kerbodyne S3-3600", Liquid, ExtraLarge, 5547.6, 7200, 2.5, 20.5, wiki = Some("Kerbodyne_S3-3600_Tank")),
    FixedFuelTank("Kerbodyne S3-7200", Liquid, ExtraLarge, 11095.2, 14400, 5, 41, wiki = Some("Kerbodyne_S3-7200_Tank")),
    FixedFuelTank("Kerbodyne S3-14400", Liquid, ExtraLarge, 16190.4, 22800, 10, 82, wiki = Some("Kerbodyne_S3-14400_Tank")),

    // Mono Propellant
    FixedFuelTank("FL-R10", Mono, Tiny, 340, 400, 0.05, 0.37, wiki = Some("FL-R10_RCS_Fuel_Tank")),
    FixedFuelTank("FL-R25", Mono, Small, 680, 800, 0.15, 1.15, wiki = Some("FL-R25_RCS_Fuel_Tank")),
    FixedFuelTank("FL-R1", Mono, Large, 400, 1300, 0.4, 3.4, wiki = Some("FL-R1_RCS_Fuel_Tank")),
    FixedFuelTank("Stratus-V Roundified Monopropellant", Mono, Radial(1, 1, 1, 1), 352, 400, 0.075, 0.315,
      wiki = Some("Stratus-V_Roundified_Monopropellant_Tank")),
    FixedFuelTank("Stratus-V Cylindrified Monopropellant", Mono, Radial(1, 1, 1, 1), 620, 800, 0.15, 0.75,
      wiki = Some("Stratus-V_Cylindrified_Monopropellant_Tank")),

    // Xenon
    FixedFuelTank("B-X50R", Xenon, Radial(1, 1, 1, 1), 600, 2200, 0.03, 0.07, wiki = Some("PB-X50R_Xenon_Container")),
    FixedFuelTank("PB-X150", Xenon, Tiny, 200, 3000, 0.05, 0.12, wiki = Some("PB-X150_Xenon_Container"))
  ))
}
