package io.github.istreeter.jsonvalidator

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object AppRoutes {

  def routes[F[_]: Sync]: HttpRoutes[F] = {
    implicit val dsl = new Http4sDsl[F]{}
    import dsl._
    val handlers = new AppHandlers[F]

    HttpRoutes.of[F] {

      case req @ POST -> Root / "schema" / schemaId =>
        handlers.handlePostSchema(schemaId, req)

    }
  }
}
