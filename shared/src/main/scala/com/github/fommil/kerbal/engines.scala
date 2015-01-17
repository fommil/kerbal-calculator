package com.github.fommil.kerbal

object Kerbin {
  /** Surface Gravity, m/s^2 */
  val g = 9.81
}

sealed trait Mount {
  /** Maximum (sensible) number of parts to fit around the payload/tank. */
  def max(tank: FuelTank): Int = 1
  def name: String = getClass.getSimpleName
}
object Mount {
  def fromName(name: String)(implicit all: Adapters): Mount = {
    for {
      adapter <- all.adapters
      mount <- List(adapter.upper, adapter.lower)
      if mount.name.equals(name)
    } yield mount
  }.head
}

case object Tiny extends Mount
case object Small extends Mount
case object Large extends Mount
case object ExtraLarge extends Mount

case class Radial(sizings: List[Int]) extends Mount {
  require(sizings.size == 4, "missing sizings")
  require(sizings.forall(_ >= 0), "negatives not allowed")
  override def max(tank: FuelTank): Int = tank.mount match {
    case Tiny => sizings(0)
    case Small => sizings(1)
    case Large => sizings(2)
    case ExtraLarge => sizings(3)
    case Radial(_) => 0 // https://github.com/fommil/kerbal-calculator/issues/9
  }
}
object Radial extends (List[Int] => Radial) {
  def apply(sizings: Int*): Radial = new Radial(sizings.toList)
}

/**
 * @param name human readable
 * @param mount kind of mounting
 * @param cost for the engine with no fuel (Funds)
 * @param mass for the engine with no fuel (kKg)
 * @param thrust (kN)
 * @param ispAtm atmosphere Kerbin specific impulse (seconds)
 * @param ispVac vacuum Kerbin specific impulse (seconds)
 * @param fuel kind of fuel to use
 * @loaded internal for engines with an internal fuel tank (kKg)
 */
case class Engine(
  name: String,
  mount: Mount,
  cost: Double,
  mass: Double,
  thrust: Double,
  ispAtm: Double,
  ispVac: Double,
  fuel: Fuel,
  internal: Option[FuelTank] = None,
  wiki: Option[String] = None
) {
  /** Effective exhaust velocity (atmosphere). */
  def veAtm: Double = Kerbin.g * ispAtm
  /** Effective exhaust velocity (vacuum). */
  def veVac: Double = Kerbin.g * ispVac

  def validTanks(implicit external: FuelTanks): Set[FuelTank] = {
    external.tanks.filter { tank =>
      fuel == tank.fuel && (mount.isInstanceOf[Radial] || tank.mount.isInstanceOf[Radial] || mount == tank.mount)
    }
  }.toSet
}
object Engine {
  // alternative constructor for engines with internal fuel tanks
  // to better match the format of the columns on the kerbal wiki
  def apply(
    name: String, m: Mount, cost: Double, mass: Double, thrust: Double, atm: Double, vac: Double, f: Fuel,
    costWithFullTank: Double, massWithFullTank: Double, wiki: Option[String]
  ): Engine = Engine(name, m, cost, mass, thrust, atm, vac, f, Some(
    FixedFuelTank(
      "Internal", f, m,
      costWithFullTank - cost, costWithFullTank,
      massWithFullTank - mass, massWithFullTank,
      unique = true
    )
  ), wiki)
}

class Engines(val engines: List[Engine])
object Engines {
  // as of 2015-01-11
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Engines
  implicit val Stock = new Engines(List(
    // Liquid Engines
    Engine("LV-1R", Radial(4, 6, 8, 12), 650, 0.03, 4, 220, 290, Liquid, wiki = Some("LV-1R_Liquid_Fuel_Engine")),
    Engine("O-10", Radial(4, 6, 8, 12), 800, 0.09, 20, 220, 290, Mono, wiki = Some("O-10_MonoPropellant_Engine")),
    Engine("Rockomax 24-77", Radial(2, 4, 6, 8), 480, 0.09, 20, 250, 300, Liquid, wiki = Some("Rockomax_24-77")),
    Engine("Rockomax Mark 55", Radial(0, 2, 4, 6), 800, 0.9, 120, 320, 360, Liquid, wiki = Some("Rockomax_Mark_55_Radial_Mount_Liquid_Engine")),
    Engine("LV-1", Tiny, 350, 0.03, 4, 220, 290, Liquid, wiki = Some("LV-1_Liquid_Fuel_Engine")),
    Engine("Rockomax 48-7S", Tiny, 300, 0.1, 30, 300, 350, Liquid, wiki = Some("Rockomax_48-7S")),
    Engine("LV-909", Small, 750, 0.5, 50, 300, 390, Liquid, wiki = Some("LV-909_Liquid_Fuel_Engine")),
    Engine("LV-T30", Small, 850, 1.25, 215, 320, 370, Liquid, wiki = Some("LV-T30_Liquid_Fuel_Engine")),
    Engine("LV-T45", Small, 950, 1.5, 200, 320, 370, Liquid, wiki = Some("LV-T45_Liquid_Fuel_Engine")),
    Engine("R.A.P.I.E.R. (Liquid)", Small, 3600, 1.2, 175, 320, 360, Liquid, wiki = Some("R.A.P.I.E.R._Engine")),
    Engine("Toroidal Aerospike", Small, 3850, 1.5, 175, 388, 390, Liquid, wiki = Some("Toroidal_Aerospike_Rocket")),
    Engine("LV-N Atomic", Small, 8700, 2.25, 60, 220, 800, Liquid, wiki = Some("LV-N_Atomic_Rocket_Motor")),
    Engine("Rockomax \"Poodle\"", Large, 1600, 2, 220, 270, 390, Liquid, wiki = Some("Rockomax_%22Poodle%22_Liquid_Engine")),
    Engine("Rockomax \"Skipper\"", Large, 2850, 3, 650, 320, 370, Liquid, wiki = Some("Rockomax_%22Skipper%22_Liquid_Engine")),
    Engine("Rockomax \"Mainsail\"", Large, 5650, 6, 1500, 320, 360, Liquid, wiki = Some("Rockomax_%22Mainsail%22_Liquid_Engine")),
    Engine("LFB KR-1x2", Large, 13462.4, 10, 2000, 290, 340, Liquid, 16400, 42, wiki = Some("LFB_KR-1x2")),
    Engine("Kerbodyne KR-2L", ExtraLarge, 20850, 6.5, 2500, 280, 380, Liquid, wiki = Some("Kerbodyne_KR-2L_Advanced_Engine")),
    Engine("S3 KS-25x4 Cluster", ExtraLarge, 32400, 9.75, 3200, 320, 360, Liquid, wiki = Some("S3_KS-25x4_Engine_Cluster")),

    // Solid Boosters
    Engine("Launch Escape System", Small, 791, 1, 750, 320, 360, Solid, 800, 1.1125, wiki = Some("Launch_Escape_System")),
    Engine("RT-10", Small, 65.2, 0.5, 250, 225, 240, Solid, 325, 3.7475, wiki = Some("RT-10_Solid_Fuel_Booster")),
    Engine("Rockomax BACC", Small, 190, 1.5, 315, 230, 250, Solid, 700, 7.875, wiki = Some("Rockomax_BACC_Solid_Fuel_Booster")),
    Engine("S1 SRB-KD25k", Small, 300, 3.0, 650, 230, 250, Solid, 1800, 21.75, wiki = Some("S1_SRB-KD25k")),
    Engine("Sepratron I", Small, 45.2, 0.0125, 18, 100, 100, Solid, 50, 0.0725, wiki = Some("Sepratron_I")),

    // Ion Engines
    Engine("PB-ION", Tiny, 5700, 0.25, 2, 0, 4200, Xenon, wiki = Some("PB-ION_Electric_Propulsion_System"))
  ))
}

case class Adapter(
  name: String,
  upper: Mount,
  lower: Mount,
  mass: Double,
  cost: Double,
  wiki: Option[String] = None
) {
  require(upper != lower, "useless converter")
  def reverse = Adapter(name, lower, upper, mass, cost, wiki)
}

class Adapters(val adapters: List[Adapter])
object Adapters {
  implicit val Stock = new Adapters(List(
    Adapter("FL-A5", Tiny, Small, 0.04, 100, wiki = Some("FL-A5_Adapter")),
    Adapter("FL-A10", Tiny, Small, 0.05, 150, wiki = Some("FL-A10_Adapter")),
    Adapter("NCS", Tiny, Small, 0.30, 320, wiki = Some("NCS_Adapter")),
    Adapter("Rockomax Brand 02", Small, Large, 0.08, 450, wiki = Some("Rockomax_Brand_Adapter_02")),
    Adapter("Rockomax Brand", Small, Large, 0.10, 500, wiki = Some("Rockomax_Brand_Adapter")),
    Adapter("Kerbodyne ADTP-2-3", Large, ExtraLarge, 0.2, 2600, wiki = Some("Kerbodyne_ADTP-2-3"))
  ).flatMap { a => List(a, a.reverse) })
}
