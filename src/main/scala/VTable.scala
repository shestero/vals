package org.shestero.vals

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

case class VTable(body: Observable[VVals], header: Option[VVals] = None) {

  def test(implicit sch: Scheduler): Task[Boolean] =
    header.map(h => body.map(_.isHeaderValid(h)).flatMap(Observable.fromTask).forallL(identity)).getOrElse(Task.pure(true))

  def csv(sep: String = ","): Observable[String] =
    header.map(_.mkLine(sep)).map(Observable.fromTask).getOrElse(Observable.empty) ++
      body.map(_.mkLine(sep)).flatMap(Observable.fromTask)

  def tsv = csv("\t")

  def tbl: Observable[(Int, String)] =
    Observable.fromIterable(LazyList.from(header.map(_ => 0).getOrElse(1))) zip tsv
}
