package org.shestero.vals

import java.time.Instant

import VT._
enum VT(typename: String) {
  case VT_unknown   extends VT("unknown")
  case VT_null      extends VT("null")
  case VT_string    extends VT("string")
  case VT_double    extends VT("double")
  case VT_long      extends VT("long")
  case VT_datetime  extends VT("datetime")
  case VT_nested    extends VT("nested")
  case VT_array     extends VT("array")

  override def toString: String = typename
}

import VVal._
object VT {
  def apply(v: VTypes): VT = v match
    case _: String  => VT_string
    case _: Double  => VT_double
    case _: Long    => VT_long
    case _: Instant => VT_datetime
    //case vs: VVals  => if (vs.isArray) VT_array else VT_nested
    case vs: VVals  => VT_nested
    case Some(v)    => apply(v)
    case None       => VT_null
    case null       => VT_null
    case _          => VT_unknown

  def apply(v: VVal): VT = apply(v.v)
}
