package com.github.fommil.kerbal

object Kerbin {
  /** Surface Gravity, m/s^2 */
  val g = 9.81
}

sealed trait Mount {
  /** Maximum (sensible) number of parts to fit around the payload/tank. */
  def max(tank: FuelTank): Int = 1
  def name: String = this.toString
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
// spaceplanes
case object Mk2 extends Mount
case object Mk3 extends Mount

case class Radial(sizings: List[Int]) extends Mount {
  require(sizings.size == 4, "missing sizings")
  require(sizings.forall(_ >= 0), "negatives not allowed")
  override def max(tank: FuelTank): Int = tank.mount match {
    case Tiny => sizings(0)
    case Small => sizings(1)
    case Large => sizings(2)
    case ExtraLarge => sizings(3)
    case _ => 0
      // maybe consider spaceplanes, but usually bad for aero.
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
 * @param thrust vacuum (kN)
 * @param ispAtm atmosphere Kerbin specific impulse (seconds)
 * @param ispVac vacuum Kerbin specific impulse (seconds)
 * @param fuel kind of fuel to use
 * @loaded internal for engines with an internal fuel tank (kKg)
 */
case class Engine(
    name: String,
    fuel: Fuel,
    mount: Mount,
    cost: Double,
    mass: Double,
    thrust: Double,
    ispAtm: Double,
    ispVac: Double,
    wiki_raw: String,
    internal: Option[FuelTank] = None
) {
  /** Effective exhaust velocity (atmosphere). */
  def veAtm: Double = Kerbin.g * ispAtm
  /** Effective exhaust velocity (vacuum). */
  def veVac: Double = Kerbin.g * ispVac

  def wiki: Option[String] = Option(wiki_raw).map(_.replace(" ", "_"))

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
    name: String, f: Fuel, m: Mount, cost: Double, mass: Double, thrust: Double, atm: Double, vac: Double,
    costWithFullTank: Double, massWithFullTank: Double, wiki: String
  ): Engine = Engine(name, f, m, cost, mass, thrust, atm, vac, wiki, Some(
    FuelTank(
      "Internal", f, m,
      0, costWithFullTank - cost,
      0, massWithFullTank - mass,
      null
    )
  ))
}

class Engines(val engines: List[Engine])
object Engines {

  private val X_Tiny = Radial(4, 6, 8, 12)
  private val X_Small = Radial(2, 4, 6, 8)
  private val X_Medium = Radial(0, 0, 2, 4)
  private val X_Large = Radial(0, 0, 0, 4)

  // as of 2022-06-13 (v1.12.3)
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Engines
  val Stock = new Engines(List(
    // Liquid engines
    Engine("Spider", Liquid, X_Tiny, 120, 0.02, 2, 260, 290, "LV-1R \"Spider\" Liquid Fuel Engine"),
    Engine("Twitch", Liquid, X_Small, 230, 0.08, 16, 275, 290, "24-77 \"Twitch\" Liquid Fuel Engine"),
    Engine("Thud", Liquid, X_Medium, 820, 0.9, 120, 275, 305, "Mk-55 \"Thud\" Liquid Fuel Engine"),
    Engine("Ant", Liquid, Tiny, 110, 0.02, 2, 80, 315, "LV-1 \"Ant\" Liquid Fuel Engine"),
    Engine("Ant (Radial)", Liquid, X_Tiny, 110, 0.02, 2, 80, 315, "LV-1 \"Ant\" Liquid Fuel Engine"),
    Engine("Spark", Liquid, Tiny, 240, 0.13, 20, 265, 320, "48-7S \"Spark\" Liquid Fuel Engine"),
    Engine("Terrier", Liquid, Small, 390, 0.5, 60, 85, 345, "LV-909 \"Terrier\" Liquid Fuel Engine"),
    Engine("Reliant", Liquid, Small, 1100, 1.25, 240, 265, 310, "LV-T30 \"Reliant\" Liquid Fuel Engine"),
    Engine("Swivel", Liquid, Small, 1200, 1.5, 215, 250, 320, "LV-T45 \"Swivel\" Liquid Fuel Engine"),
    Engine("Vector", Liquid, Small, 18000, 4, 1000, 295, 315, "S3 KS-25 \"Vector\" Liquid Fuel Engine"),
    Engine("Vector (Radial)", Liquid, X_Small, 18000, 4, 1000, 295, 315, "S3 KS-25 \"Vector\" Liquid Fuel Engine"),
    Engine("Dart", Liquid, Small, 3850, 1, 180, 290, 340, "T-1 Toroidal Aerospike \"Dart\" Liquid Fuel Engine"),
    Engine("Dart (Radial)", Liquid, X_Small, 3850, 1, 180, 290, 340, "T-1 Toroidal Aerospike \"Dart\" Liquid Fuel Engine"),
    Engine("Poodle", Liquid, Large, 1300, 1.75, 250, 90, 350, "RE-L10 \"Poodle\" Liquid Fuel Engine"),
    Engine("Skipper", Liquid, Large, 5300, 3, 650, 280, 320, "RE-I5 \"Skipper\" Liquid Fuel Engine"),
    Engine("Mainsail", Liquid, Large, 13000, 6, 1500, 285, 310, "RE-M3 \"Mainsail\" Liquid Fuel Engine"),
    Engine("Twin-Boar", Liquid, Large, 14062.4, 10.5, 2000, 280, 300, 17000, 42.50, "LFB KR-1x2 \"Twin-Boar\" Liquid Fuel Engine"),
    Engine("Twin-Boar (Radial)", Liquid, X_Large, 14062.4, 10.5, 2000, 280, 300, 17000, 42.50, "LFB KR-1x2 \"Twin-Boar\" Liquid Fuel Engine"),
    Engine("Rhino", Liquid, ExtraLarge, 25000, 9, 2000, 205, 340, "Kerbodyne KR-2L+ \"Rhino\" Liquid Fuel Engine"),
    Engine("Mammoth", Liquid, ExtraLarge, 39000, 15, 4000, 295, 315, "S3 KS-25x4 \"Mammoth\" Liquid Fuel Engine"),
    Engine("RAPIER [Mix]", Liquid, Small, 6000, 2, 180, 275, 305, "CR-7 R.A.P.I.E.R. Engine"),

    // Liquid (Nuclear)
    Engine("Nerv", LiquidOnly, Small, 10000, 3, 60, 185, 800, "LV-N \"Nerv\" Atomic Rocket Motor"),

    // Jet (LiquidOnly but need an atmosphere). hack sets LSP to 0 in vac and
    // use Mach 0 thrust but best to consult the velocity curves for these to
    // really make a decision.
    // Engine("Juno", LiquidOnly, Tiny, 450, 0.25, 20, 6400, 0, "J-20 \"Juno\" Basic Jet Engine"),
    // Engine("Wheesley", LiquidOnly, Small, 1400, 1.5, 120, 10500, 0, "J-33 \"Wheesley\" Turbofan Engine"),
    // Engine("Panther", LiquidOnly, Small, 2000, 1.2, 85, 9000, 0, "J-404 \"Panther\" Afterburning Turbofan"),
    // Engine("Whiplash", LiquidOnly, Small, 2250, 1.8, 130, 4000, 0, "J-X4 \"Whiplash\" Turbo Ramjet Engine"),
    // Engine("Goliath", LiquidOnly, X_Small, 2600, 4.5, 360, 12600, 0, "J-90 \"Goliath\" Turbofan Engine"),
    // Engine("RAPIER [Jet]", LiquidOnly, Small, 6000, 2, 105, 3200, 0, "CR-7 R.A.P.I.E.R. Engine"),

    Engine("Puff", Mono, X_Small, 150, 0.09, 20, 120, 250, "O-10 \"Puff\" MonoPropellant Fuel Engine"),
    // not counting RCS, which could be viable for really small craft

    // Ion engine (Xenon)
    Engine("Dawn", Xenon, X_Tiny, 8000, 0.25, 2.0, 100, 4200, "IX-6315 \"Dawn\" Electric Propulsion System"),

    // Solid boosters),
    Engine("Flea (Inline)", Solid, Small, 116, 0.45, 192, 140, 165, 200, 1.5, "RT-5 \"Flea\" Solid Fuel Booster"),
    Engine("Hammer (Inline)", Solid, Small, 175, 0.75, 227, 170, 195, 400, 3.56, "RT-10 \"Hammer\" Solid Fuel Booster"),
    Engine("Thumper (Inline)", Solid, Small, 358, 1.5, 300, 175, 210, 850, 7.65, "BACC \"Thumper\" Solid Fuel Booster"),
    Engine("Kickback (Inline)", Solid, Small, 1140, 4.5, 670, 195, 220, 2700, 24, "S1 SRB-KD25k \"Kickback\" Solid Fuel Booster"),
    Engine("Mite (Inline)", Solid, Tiny, 51, 0.075, 12.5, 185, 210, 75, 0.375, "FM1 \"Mite\" Solid Fuel Booster"),
    Engine("Shrip (Inline)", Solid, Tiny, 96, 0.155, 30, 190, 215, 150, 0.875, "F3S0 \"Shrimp\" Solid Fuel Booster"),
    Engine("Thoroughbred (Inline)", Solid, Large, 4200, 10, 1700, 205, 230, 9000, 70, "S2-17 \"Thoroughbred\" Solid Fuel Booster"),
    Engine("Clydesdale (Inline)", Solid, Large, 8660, 21, 3300, 210, 235, 18500, 144, "S2-33 \"Clydesdale\" Solid Fuel Booster"),
    Engine("Flea", Solid, X_Small, 116, 0.45, 192, 140, 165, 200, 1.5, "RT-5 \"Flea\" Solid Fuel Booster"),
    Engine("Hammer", Solid, X_Small, 175, 0.75, 227, 170, 195, 400, 3.56, "RT-10 \"Hammer\" Solid Fuel Booster"),
    Engine("Thumper", Solid, X_Small, 358, 1.5, 300, 175, 210, 850, 7.65, "BACC \"Thumper\" Solid Fuel Booster"),
    Engine("Kickback", Solid, X_Small, 1140, 4.5, 670, 195, 220, 2700, 24, "S1 SRB-KD25k \"Kickback\" Solid Fuel Booster"),
    Engine("Sepratron", Solid, X_Tiny, 70.2, 0, 18, 118, 154, 75, 0.1, "Sepratron I"),
    Engine("Mite", Solid, X_Tiny, 51, 0.075, 12.5, 185, 210, 75, 0.375, "FM1 \"Mite\" Solid Fuel Booster"),
    Engine("Shrip", Solid, X_Tiny, 96, 0.155, 30, 190, 215, 150, 0.875, "F3S0 \"Shrimp\" Solid Fuel Booster"),
    Engine("Thoroughbred", Solid, X_Large, 4200, 10, 1700, 205, 230, 9000, 70, "S2-17 \"Thoroughbred\" Solid Fuel Booster"),
    Engine("Clydesdale", Solid, X_Large, 8660, 21, 3300, 210, 235, 18500, 144, "S2-33 \"Clydesdale\" Solid Fuel Booster")
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
      // known limitation: doesn't account for fuel in the adapter
      // internal: Option[FuelTank]
) {
  require(upper != lower, "useless converter")
  def reverse = Adapter(name, lower, upper, mass, cost, wiki)
}

class Adapters(val adapters: List[Adapter])
object Adapters {
  // known limitation: couplers are not supported (e.g. mount 4x of something)

  // as of 2022-06-13 (v1.12.3)
  // http://wiki.kerbalspaceprogram.com/wiki/Parts#Structural
  val Stock = new Adapters(List(
    Adapter("FL-A10", Tiny, Small, 0.05, 150, wiki = Some("FL-A10_Adapter")),
    Adapter("Rockomax Brand", Small, Large, 0.10, 500, wiki = Some("Rockomax_Brand_Adapter")),
    Adapter("FL-A5", Tiny, Small, 0.04, 100, wiki = Some("FL-A5_Adapter")),
    Adapter("Rockomax Brand 02", Small, Large, 0.08, 450, wiki = Some("Rockomax_Brand_Adapter_02")),

    // really fuel tanks
    Adapter("Kerbodyne ADTP-2-3", Large, ExtraLarge, 1.88, 246, wiki = Some("Kerbodyne_ADTP-2-3")),
    // missing all the remaining adapters that are also fuel tanks...
  ).flatMap { a => List(a, a.reverse) })
}
