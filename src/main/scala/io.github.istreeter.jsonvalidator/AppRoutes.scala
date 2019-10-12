package io.github.istreeter.jsonvalidator

import cats.effect.Sync
import cats.implicits._
import org.http4s.{HttpRoutes, Method}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Allow

object AppRoutes {

  def routes[F[_]: Sync](cache: SchemaCache[F]): HttpRoutes[F] = {
    implicit val dsl = new Http4sDsl[F]{}
    import dsl._
    val handlers = new AppHandlers[F](cache)

    HttpRoutes.of[F] {

      case req @ POST -> Root / "schema" / schemaId =>
        handlers.handlePostSchema(schemaId, req)

      case (GET | HEAD) -> Root / "schema" / schemaId =>
        handlers.handleGetSchema(schemaId)

      case (_ : Method) -> Root / "schema" / _ =>
        MethodNotAllowed(Allow(GET, HEAD, POST))

      case req @ POST -> Root / "validate" / schemaId =>
        handlers.handleValidation(schemaId, req)

      case (_ : Method) -> Root / "validate" =>
        MethodNotAllowed(Allow(POST))

    }
  }
}
