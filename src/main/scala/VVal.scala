package org.shestero.vals

import java.time.Instant
import scala.util.Try

import VT._
import VVal._
case class VVal(v: VTypes, tag: VTag = emptyTag, attrs: VAttrs = emptyAttrs) {
  def get[T]: T = v.asInstanceOf[T]

  def lift: Option[VVal] = Try {
    VVal(get[Option[VTypes0]].get)
  }.toOption

  def compare(other: VVal): Double =
    (v, other.v) match
      case (None, None) | (None, _) | (_, None) => Double.NegativeInfinity
      case (Some(v1), Some(v2)) => VVal(v1) compare VVal(v2)
      case (_, Some(v2)) => this compare VVal(v2) // this compare other.lift.get
      case (Some(v1), _) => VVal(v1) compare this // lift.get compare this
      case _ =>
    if (VT(this) == VT(other))
      VT(v) match
        case VT_string   => get[String] compareToIgnoreCase other.get[String]
        case VT_double   => get[Double] - other.get[Double]
        case VT_long     => get[Long] - other.get[Long]
        case VT_datetime => get[Instant] compareTo other.get[Instant]
        case _ => Double.PositiveInfinity // VVal(get.toString) compare VVal(other.get.toString)
    else
      (VT(this), VT(other)) match
        case (VT_double, VT_long) => get[Double] - other.get[Long].toDouble
        case (VT_long, VT_double) => get[Long].toDouble - other.get[Double]
        case _ => VVal(get.toString) compare VVal(other.get.toString)
}

object VVal {
  type VTypes0 = String | Double | Long | Instant   // generic types
  type VTypes  = VTypes0 | Option[VTypes0] | VVals  // available types

  type VTag    = String
  type VAttrs  = Map[String, String]

  val emptyTag = ""
  val emptyAttrs = Map.empty[String, String]

  def apply(v: Int) = new VVal(v.toLong)
}
