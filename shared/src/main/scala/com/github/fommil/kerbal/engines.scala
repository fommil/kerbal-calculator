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
  def fromName(name: String)(implicit all: Adapters): Option[Mount] = {
    for {
      adapter <- all.adapters
      mount <- List(adapter.upper, adapter.lower)
      if mount.name.startsWith(name)
    } yield mount
  }.headOption
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
 * TODO: add atm thrust
 *
 * @param name human readable
 * @param mount kind of mounting
 * @param cost for the engine with no fuel (Funds)
 * @param mass for the engine with no fuel (kKg)
 * @param thrust vacuum (kN)
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
      0, costWithFullTank - cost,
      0, massWithFullTank - mass,
      unique = true
    )
  ), wiki)
}

class Engines(val engines: List[Engine])
object Engines {
  // as of 2015-08-08
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Engines
  implicit val Stock = new Engines(List(
    // Liquid Engines
    Engine("Spider", Radial(4, 6, 8, 12), 120, 0.02, 2, 260, 290, Liquid, wiki = Some("""LV-1R_"Spider"_Liquid_Fuel_Engine""")),
    Engine("Twitch", Radial(2, 4, 6, 8), 400, 0.09, 16, 250, 290, Liquid, wiki = Some("""24-77_"Twitch"_Liquid_Fuel_Engine""")),
    Engine("Thud", Radial(0, 2, 4, 6), 820, 0.9, 120, 275, 305, Liquid, wiki = Some("""Mk-55_"Thud"_Liquid_Fuel_Engine""")),
    Engine("Puff", Radial(4, 6, 8, 12), 150, 0.09, 20, 120, 250, Mono, wiki = Some("""O-10_"Puff"_MonoPropellant_Fuel_Engine""")),
    Engine("Ant", Tiny, 110, 0.02, 2, 80, 315, Liquid, wiki = Some("""LV-1_"Ant"_Liquid_Fuel_Engine""")),
    Engine("Spark", Tiny, 200, 0.1, 18, 270, 300, Liquid, wiki = Some("""48-7S_"Spark"_Liquid_Fuel_Engine""")),
    Engine("Terrier", Small, 390, 0.5, 60, 85, 345, Liquid, wiki = Some("""LV-909_"Terrier"_Liquid_Fuel_Engine""")),
    Engine("Reliant", Small, 1100, 1.25, 215, 280, 300, Liquid, wiki = Some("""LV-T30_"Reliant"_Liquid_Fuel_Engine""")),
    Engine("Swivel", Small, 1200, 1.5, 200, 270, 320, Liquid, wiki = Some("""LV-T45_"Swivel"_Liquid_Fuel_Engine""")),
    Engine("Rapier (Liquid)", Small, 6000, 2, 180, 275, 305, Liquid, wiki = Some("""CR-7_R.A.P.I.E.R._Engine""")),
    Engine("Aerospike", Small, 3850, 1, 180, 290, 340, Liquid, wiki = Some("""T-1_Toroidal_"Aerospike"_Liquid_Fuel_Engine""")),
    Engine("Nerv", Small, 10000, 3, 60, 185, 800, Liquid, wiki = Some("""LV-N_"Nerv"_Atomic_Rocket_Motor""")),
    Engine("Poodle", Large, 1300, 1.75, 250, 90, 350, Liquid, wiki = Some("""RE-L10_"Poodle"_Liquid_Fuel_Engine""")),
    Engine("Skipper", Large, 5300, 3, 650, 280, 320, Liquid, wiki = Some("""RE-I5_"Skipper"_Liquid_Fuel_Engine""")),
    Engine("Mainsail", Large, 13000, 6, 1500, 285, 310, Liquid, wiki = Some("""RE-M3_"Mainsail"_Liquid_Engine""")),
    Engine("Twin-Boar", Large, 14062.4, 10, 2000, 280, 300, Liquid, 17000, 42, wiki = Some("""LFB_KR-1x2_"Twin-Boar"_Liquid_Fuel_Engine""")),
    Engine("Rhino", ExtraLarge, 25000, 9, 2000, 255, 340, Liquid, wiki = Some("""Kerbodyne_KR-2L+_"Rhino"_Liquid_Fuel_Engine""")),
    Engine("Mammoth", ExtraLarge, 39000, 15, 4000, 395, 315, Liquid, wiki = Some("""S3_KS-25x4_"Mammoth"_Liquid_Fuel_Engine""")),

    // Solid Boosters
    Engine("Flea", Small, 116, 0.45, 192, 140, 165, Solid, 200, 1.5, wiki = Some("""RT-5_"Flea"_Solid_Fuel_Booster""")),
    Engine("Hammer", Small, 175, 0.75, 227, 170, 195, Solid, 400, 3.56, wiki = Some("""RT-10_"Hammer"_Solid_Fuel_Booster""")),
    Engine("Thumper", Small, 358, 1.5, 300, 175, 210, Solid, 850, 7.65, wiki = Some("""BACC "Thumper" Solid Fuel Booster""")),
    Engine("Kickback", Small, 1140, 4.5, 670, 195, 220, Solid, 2700, 24, wiki = Some("""S1_SRB-KD25k_"Kickback"_Solid_Fuel_Booster""")),
    Engine("Sepratron I", Small, 70, 0.0125, 18, 118, 154, Solid, 75, 0.0725, wiki = Some("""Sepratron_I""")),
    Engine("LES", Small, 982, 0.9, 750, 160, 180, Solid, 1000, 1.13, wiki = Some("""Launch_Escape_System""")),

    // HACK addressing https://github.com/fommil/kerbal-calculator/issues/9
    Engine("Hammer (Radial)", Radial(0, 2, 4, 6), 175, 0.75, 227, 170, 195, Solid, 400, 3.56, wiki = Some("""RT-10_"Hammer"_Solid_Fuel_Booster""")),
    Engine("Thumper (Radial)", Radial(0, 2, 4, 6), 358, 1.5, 300, 175, 210, Solid, 850, 7.65, wiki = Some("""BACC "Thumper" Solid Fuel Booster""")),
    Engine("Kickback (Radial)", Radial(0, 2, 4, 6), 1140, 4.5, 670, 195, 220, Solid, 2700, 24, wiki = Some("""S1_SRB-KD25k_"Kickback"_Solid_Fuel_Booster""")),
    Engine("Sepratron I (Radial)", Radial(4, 6, 8, 12), 70, 0.0125, 18, 118, 154, Solid, 75, 0.0725, wiki = Some("""Sepratron_I""")),

    // Ion Engines
    Engine("Dawn", Tiny, 8000, 0.25, 2, 100, 4200, Xenon, wiki = Some("""IX-6315 "Dawn" Electric Propulsion System"""))
  ))

  def fromName(name: String)(implicit all: Engines): Option[Engine] =
    all.engines.find(_.name == name)
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
    Adapter("FL-A10", Tiny, Small, 0.05, 150, wiki = Some("FL-A10_Adapter")),
    Adapter("Rockomax Brand", Small, Large, 0.10, 500, wiki = Some("Rockomax_Brand_Adapter")),
    Adapter("Kerbodyne ADTP-2-3", Large, ExtraLarge, 0.2, 2600, wiki = Some("Kerbodyne_ADTP-2-3")),
    Adapter("FL-A5", Tiny, Small, 0.04, 100, wiki = Some("FL-A5_Adapter")),
    Adapter("Rockomax Brand 02", Small, Large, 0.08, 450, wiki = Some("Rockomax_Brand_Adapter_02")),
    Adapter("NCS", Small, Tiny, 0.50, 320, wiki = Some("NCS_Adapter"))
  ).flatMap { a => List(a, a.reverse) })
}
