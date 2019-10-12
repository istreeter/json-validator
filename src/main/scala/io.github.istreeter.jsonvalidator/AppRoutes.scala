package io.github.istreeter.jsonvalidator

import cats.effect.Sync
import cats.implicits._
import org.http4s.{HttpRoutes, Method}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Allow

class AppRoutes[F[_]: Sync] {

  implicit val dsl = new Http4sDsl[F]{}

  import AppRoutes._
  import dsl._

  def routes(store: SchemaStore[F]): HttpRoutes[F] = {
    val handlers = new AppHandlers[F](store)

    HttpRoutes.of[F] {

      case req @ POST -> Root / Schema / schemaId =>
        handlers.handlePostSchema(schemaId, req)

      case (GET | HEAD) -> Root / Schema / schemaId =>
        handlers.handleGetSchema(schemaId)

      case (_ : Method) -> Root / Schema / _ =>
        MethodNotAllowed(Allow(GET, HEAD, POST))

      case req @ POST -> Root / Validate / schemaId =>
        handlers.handleValidation(schemaId, req)

      case (_ : Method) -> Root / Validate =>
        MethodNotAllowed(Allow(POST))

    }
  }
}

object AppRoutes {

  def apply[F[_] : Sync] : AppRoutes[F] = new AppRoutes[F]

  val Schema : String = "schema"
  val Validate : String = "validate"

}
