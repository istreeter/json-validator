package io.github.istreeter.jsonvalidator

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object AppRoutes {

  def routes[F[_]: Sync](cache: SchemaCache[F]): HttpRoutes[F] = {
    implicit val dsl = new Http4sDsl[F]{}
    import dsl._
    val handlers = new AppHandlers[F](cache)

    HttpRoutes.of[F] {

      case req @ POST -> Root / "schema" / schemaId =>
        handlers.handlePostSchema(schemaId, req)

      case req @ GET -> Root / "schema" / schemaId =>
        handlers.handleGetSchema(schemaId)

    }
  }
}
