package io.github.istreeter.jsonvalidator

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object AppServer {

  def stream[F[_]: ConcurrentEffect](store: SchemaStore[F])
                                    (implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    val httpApp = (
      AppRoutes[F].routes(store)
    ).orNotFound

    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(finalHttpApp)
      .serve
  }.drain
}
