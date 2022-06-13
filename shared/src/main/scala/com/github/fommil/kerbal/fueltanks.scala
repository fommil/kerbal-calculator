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
case object LiquidOnly extends Fuel {
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

/**
 * @param baseMass mass without any fuel
 * @param baseCost cost without any fuel
 * @param fullCost cost with a full tank
 * @param unique is only one allowed
 */
case class FuelTank(
    name: String,
    fuel: Fuel,
    mount: Mount, // known limitation: doesn't consider brand adapters
    baseCost: Double,
    fullCost: Double,
    emptyMass: Double,
    fullMass: Double,
    // name on Kerbal wiki, may be null
    raw_wiki_name: String
) {
  require(fullMass > 0, name)
  require(emptyMass >= 0, name)
  require(fullMass > emptyMass, name)

  def wiki: Option[String] = Option(raw_wiki_name).map(_.replace(" ", "_"))

  // max supplied fuel (kKg) per container
  def max = fullMass - emptyMass

  // cost (Funds) for container with requested fuel (kKg)
  def cost(amount: Double): Double = {
    require(amount >= 0 && amount <= max)
    val fuelCost = fullCost - baseCost
    baseCost + fuelCost * amount / max
  }

  // mass (kKg) for container with requested fuel (kKg)
  def mass(amount: Double): Double = {
    require(amount >= 0 && amount <= max, s"$amount was outside [0, $max]")
    emptyMass + amount
  }
}

class FuelTanks(val tanks: List[FuelTank])
object FuelTanks {
  private val X = Radial(1, 1, 1, 1)

  // as of 2022-06-13 (v1.12.3)
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Fuel_Tanks
  val Stock = new FuelTanks(List(
    // Liquid + Oxygen
    FuelTank("Dumpling", Liquid, X, 39.9, 50, 0.0138, 0.1238, "R-4 'Dumpling' External Tank"),
    FuelTank("Baguette", Liquid, X, 25.21, 50, 0.0338, 0.3038, "R-11 'Baguette' External Tank"),
    FuelTank("Doughnut", Liquid, Small, 119.46, 147, 0.0375, 0.3375, "R-12 'Doughnut' External Tank"),
    FuelTank("Oscar-B", Liquid, Tiny, 51.64, 70, 0.025, 0.225, "Oscar-B Fuel Tank"),
    FuelTank("FL-T100", Liquid, Small, 104.1, 150, 0.0625, 0.5625, "FL-T100 Fuel Tank"),
    FuelTank("FL-T200", Liquid, Small, 183.2, 275, 0.125, 1.125, "FL-T200 Fuel Tank"),
    FuelTank("FL-T400", Liquid, Small, 316.4, 500, 0.25, 2.25, "FL-T400 Fuel Tank"),
    FuelTank("FL-T800", Liquid, Small, 432.8, 800, 0.5, 4.5, "FL-T800 Fuel Tank"),
    FuelTank("X200-8", Liquid, Large, 432.8, 800, 0.5, 4.5, "Rockomax X200-8 Fuel Tank"),
    FuelTank("X200-16", Liquid, Large, 815.6, 1550, 1, 9, "Rockomax X200-16 Fuel Tank"),
    FuelTank("X200-32", Liquid, Large, 1531.2, 3000, 2, 18, "Rockomax X200-32 Fuel Tank"),
    FuelTank("Jumbo-64", Liquid, Large, 2812.4, 5750, 4, 36, "Rockomax Jumbo-64 Fuel Tank"),
    FuelTank("S3-3600", Liquid, ExtraLarge, 1597.6, 3250, 2.25, 20.25, "Kerbodyne S3-3600 Tank"),
    FuelTank("S3-7200", Liquid, ExtraLarge, 3195.2, 6500, 4.5, 40.5, "Kerbodyne S3-7200 Tank"),
    FuelTank("S3-14400", Liquid, ExtraLarge, 6390.4, 13000, 9, 81, "Kerbodyne S3-14400 Tank"),
    FuelTank("Mk2 [Mix] (Short)", Liquid, Mk2, 566.4, 750, 0.29, 2.29, "Mk2 Rocket Fuel Fuselage Short"),
    FuelTank("Mk2 [Mix]", Liquid, Mk2, 1082.8, 1450, 0.57, 4.57, "Mk2 Rocket Fuel Fuselage"),
    FuelTank("Mk3 [Mix] (Short)", Liquid, Mk3, 1352.5, 2500, 1.79, 14.29, "Mk3 Rocket Fuel Fuselage Short"),
    FuelTank("Mk3 [Mix]", Liquid, Mk3, 2705, 5000, 3.57, 28.57, "Mk3 Rocket Fuel Fuselage"),
    FuelTank("Mk3 [Mix] (Long)", Liquid, Mk3, 5410, 10000, 7.14, 57.14, "Mk3 Rocket Fuel Fuselage Long"),

    // Pure Liquid (Jet and Nuclear)
    FuelTank("Mk0 [Jet]", LiquidOnly, Tiny, 160, 200, 0.025, 0.275, "Mk0 Liquid Fuel Fuselage"),
    FuelTank("Mk1 [Jet]", LiquidOnly, Small, 230, 550, 0.25, 2.25, "Mk1 Liquid Fuel Fuselage"),
    FuelTank("Mk2 [Jet] Short", LiquidOnly, Mk2, 430, 750, 0.29, 2.29, "Mk2 Liquid Fuel Fuselage Short"),
    FuelTank("Mk2 [Jet]", LiquidOnly, Mk2, 810, 1450, 0.57, 4.57, "Mk2 Liquid Fuel Fuselage"),
    FuelTank("Mk3 [Jet] (Short)", LiquidOnly, Mk3, 2300, 4300, 1.79, 14.29, "Mk3 Liquid Fuel Fuselage Short"),
    FuelTank("Mk3 [Jet]", LiquidOnly, Mk3, 4600, 8600, 3.57, 28.57, "Mk3 Liquid Fuel Fuselage"),
    FuelTank("Mk3 [Jet] (Long)", LiquidOnly, Mk3, 9200, 17200, 7.14, 57.14, "Mk3 Liquid Fuel Fuselage Long"),

    // Monopropellant
    FuelTank("FL-R20", Mono, Tiny, 176, 200, 0.02, 0.1, "FL-R20 RCS Fuel Tank"),
    FuelTank("FL-R120", Mono, Small, 186, 330, 0.08, 0.56, "FL-R120 RCS Fuel Tank"),
    FuelTank("FL-R750", Mono, Large, 900, 1800, 0.4, 3.4, "FL-R750 RCS Fuel Tank"),
    FuelTank("Mk2 [Mono]", Mono, Mk2, 270, 750, 0.29, 1.89, "Mk2 Monopropellant Tank"),
    FuelTank("Mk3 [Mono]", Mono, Mk3, 2520, 5040, 1.4, 9.8, "Mk3 Monopropellant Tank"),
    FuelTank("Stratus-V Roundified", Mono, X, 176, 200, 0.02, 0.1, "Stratus-V Roundified Monopropellant Tank"),
    FuelTank("Stratus-V Cylindrified", Mono, X, 190, 250, 0.03, 0.23, "Stratus-V Cylindrified Monopropellant Tank"),

    // Xenon
    FuelTank("PB-X50R", Xenon, X, 600, 2220, 0.014, 0.054, "PB-X50R Xenon Container"),
    FuelTank("PB-X150", Xenon, Tiny, 800, 3680, 0.024, 0.1, "PB-X150 Xenon Container"),
    FuelTank("PB-X750", Xenon, Small, 1500, 24300, 0.19, 0.76, "PB-X750 Xenon Container")
  ))
}
