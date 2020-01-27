package com.github.allter

import cats.{Applicative, Apply, Monad, Parallel}
import cats.effect.{Concurrent, ContextShift, Fiber}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.apply._
import fs2.Stream

package object evalmapbg {
  def toBackground[F[_]: Apply: Concurrent: ContextShift, A](io: F[A]): F[Fiber[F, A]] =
    Concurrent[F].start( ContextShift[F].shift *> io )

  def evalMapBackground[F[_]: Monad: Parallel: Concurrent: ContextShift, A, B ](s: Stream[F, A] )(handler1: A => F[B] ): Stream[F, B] = {

    s.noneTerminate
      .evalMapAccumulate[F, Option[Fiber[F,B]], Option[B]](None)( (acc, o) =>
        acc match {
          case None =>
            o match {
              case None =>
                Applicative[F].pure( None, None )
              case Some (a) => {
                //println(s"Forking first handler for arg $a...")
                toBackground(handler1(a))
                  .flatMap { fib =>
                    Applicative[F].pure(Some(fib), None)
                  }
              }
            }
          case Some(fiob1) =>
            o match {
              case None =>
                fiob1.join.map { b =>
                  //println(s"Joined the last value $b.")
                  (None, Some(b))
                }
              case Some(a2) =>
                fiob1.join.flatMap { b1 =>
                  //println(s"Forking second handler for arg $a2 and joined result $b1...")
                  toBackground(handler1(a2)).flatMap( fib2 =>
                    Applicative[F].pure(Some(fib2), Some(b1))
                  )
                }
            }
        }
      ).map(_._2)
      .unNone
  }
}
