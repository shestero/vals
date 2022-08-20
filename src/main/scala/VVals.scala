package org.shestero.vals

import cats.syntax.apply._
import monix.eval.Task
import monix.reactive.Observable

import scala.annotation.targetName

import VT._
import VVal._

case class VVals(vs: Observable[VVal], isArray: Boolean = true, classTag: String = emptyTag) {
  def get = vs

  def skipNulls = get.collect(vv => vv.get[VTypes] match {
    case Some(v) => VVal(v)
    case v: VTypes0 => vv
  })

  def size: Task[Int] = get.toListL.map(_.size)

  def keys: Observable[VTag] = get.map(_.tag)

  def isKeysValid: Task[Boolean] = keys.toListL.map(!_.contains(emptyTag))

  def isKeysUnique: Task[Boolean] = keys.toListL.map(keys => keys.distinct.length == keys.length)

  def niceHeader: Task[Boolean] = (isKeysValid, isKeysUnique, vs.map(VT(_)).forallL(Seq(VT_string, VT_long).contains)).mapN(_ && _ && _)

  def isHeaderValid(h: VVals): Task[Boolean] = ((keys.toListL, h.keys.toListL).mapN(_.toSet == _.toSet), niceHeader).mapN(_ && _)

  def sameType: Task[Boolean] = skipNulls.map(VT(_)).toListL.map(_.distinct.length <= 1)

  def filterType(t: VT): VVals = VVals(get.filter(VT(_) == t))

  def arrange(center: VVal): Task[Seq[(VVal, Double)]] = skipNulls.map(v => v -> (center compare v))
    .filter(_._2 != Double.PositiveInfinity)
    .filter(_._2 != Double.NegativeInfinity)
    .toListL.map(_.sortBy(_._2.abs))

  def typed: Observable[(VVal, VT)] = vs.map { v => v -> VT(v) }
  def tagged: Observable[(VVal, String)] = vs.map { v => v -> v.tag }

  def mkLine(sep: String = ","): Task[String] = get.toListL.map(_.mkString(sep))
  def mkValueLine(sep: String = ","): Task[String] = get.toListL.map(_.map(_.v).mkString(sep))
  def mkTypedLine(sep: String = ","): Task[String] = typed.toListL.map(_.map{ _.v.toString ++ ":" ++ _.toString }.mkString(sep))
  def mkTaggedLine(sep: String = ","): Task[String] = tagged.toListL.map(_.map{ _.v.toString ++ "(" ++ _ ++ ")" }.mkString(sep))

  def get(tag: String): VVals = VVals(vs.filter(_.tag == tag))
  def get(i: Int): VVals = get(i.toString)
}

object VVals {
  def fromIterable(vals: Iterable[VVal], isArray: Boolean = true, classTag: String = emptyTag): VVals =
    new VVals(Observable.fromIterable(vals), isArray, classTag)

  @targetName("apply1") def apply(vals: VVal*): VVals = new VVals(Observable.fromIterator(Task {
    vals.iterator
  }), false)

  @targetName("apply2") def apply(vals: VTypes | Int*): VVals =
    new VVals(Observable.fromIterator(Task {
      vals.zip(LazyList.from(1)).map((v, i) => v match {
        case v: Int => VVal.apply(v).copy(tag = i.toString)
        case v: VTypes => VVal(v, i.toString)
      }
      ).iterator
    }), false)
}
