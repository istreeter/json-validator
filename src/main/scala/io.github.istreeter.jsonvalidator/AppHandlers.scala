package io.github.istreeter.jsonvalidator

import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import org.http4s.{MalformedMessageBodyFailure, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.json4s.jackson._
import org.json4s.JValue

class AppHandlers[F[_] : Sync : Http4sDsl](cache: SchemaCache[F]) {

  val dsl = implicitly[Http4sDsl[F]]
  import dsl._

  def deserialize(request: Request[F], onError: => JValue) : F[Either[Response[F], JValue]] =
    request.as[JValue]
      .map(Right(_) : Either[Response[F], JValue])
      .handleErrorWith {
        case e : MalformedMessageBodyFailure =>
          BadRequest(onError).map(Left(_))
      }

  def handlePostSchema(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        schema <- EitherT(deserialize(request, Responses.schemaUploadErrorResponse(schemaId)))
        _ <- EitherT.right(cache.putSchema(schemaId, schema))
        content = Responses.schemaUploadResponse(schemaId)
        resp <- EitherT.right(Ok(content))
      } yield resp

    eitherT.merge
  }

}
