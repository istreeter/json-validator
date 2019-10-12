package io.github.istreeter.jsonvalidator

import cats.data.{EitherT, OptionT}
import cats.effect.Sync
import cats.implicits._
import org.http4s.{MalformedMessageBodyFailure, MediaType, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.json4s.jackson._
import org.http4s.headers.`Content-Type`
import org.json4s.JValue
import org.json4s.jackson.JsonMethods._

class AppHandlers[F[_] : Sync : Http4sDsl](cache: SchemaCache[F]) {

  val dsl = implicitly[Http4sDsl[F]]
  import dsl._

  def handlePostSchema(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        schema <- EitherT(deserialize(request, Responses.schemaUploadErrorResponse(schemaId)))
        json = compact(render(schema))
        _ <- EitherT.right(cache.put(schemaId, json))
        content = Responses.schemaUploadResponse(schemaId)
        resp <- EitherT.right(Ok(content))
      } yield resp

    eitherT.merge
  }

  def handleGetSchema(schemaId: String) : F[Response[F]] = {

    val optionT : OptionT[F, Response[F]] =
      for {
        json <- OptionT(cache.get(schemaId))
        resp <- OptionT.liftF(Ok(json, `Content-Type`(MediaType.application.json)))
      } yield resp

    optionT.getOrElseF(NotFound())
  }

  private def deserialize(request: Request[F], onError: => JValue) : F[Either[Response[F], JValue]] =
    request.as[JValue]
      .map(Right(_) : Either[Response[F], JValue])
      .handleErrorWith {
        case e : MalformedMessageBodyFailure =>
          BadRequest(onError).map(Left(_))
      }


}
