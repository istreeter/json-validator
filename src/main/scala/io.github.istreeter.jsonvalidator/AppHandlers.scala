package io.github.istreeter.jsonvalidator

import cats.Applicative
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import org.http4s.{MalformedMessageBodyFailure, MediaType, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.json4s.jackson._
import org.http4s.headers.`Content-Type`
import org.json4s.JValue
import org.json4s.jackson.JsonMethods

class AppHandlers[F[_] : Sync](cache: SchemaCache[F])(implicit dsl: Http4sDsl[F]) {

  import dsl._

  def handlePostSchema(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        schema <- EitherT(deserialize(request, Responses.schemaUploadErrorResponse(schemaId)))
        json = JsonMethods.compact(schema)
        _ <- EitherT.right(cache.put(schemaId, json))
        content = Responses.schemaUploadResponse(schemaId)
        resp <- EitherT.right(Created(content))
      } yield resp

    eitherT.merge
  }

  def handleGetSchema(schemaId: String) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        json <- EitherT(getSchema(schemaId))
        resp <- EitherT.right(Ok(json, `Content-Type`(MediaType.application.json)))
      } yield resp

    eitherT.merge

  }

  def handleValidation(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        document <- EitherT(deserialize(request, Responses.validationErrorResponse(schemaId, "Invalid JSON")))
        schemaJson <- EitherT(getSchema(schemaId))
        schema = JsonMethods.parse(schemaJson)
        result = ValidationUtils.validate(schema, document)
        resp <- EitherT.right(toValidationResponse(schemaId, result))
      } yield resp

    eitherT.merge
  }

  private def toValidationResponse(schemaId: String, result: Either[Iterable[String], Unit]): F[Response[F]] =
    result match {
      case Right(()) =>
        val content = Responses.validationOkResponse(schemaId)
        Ok(content)
      case Left(errors) =>
        val content = Responses.validationErrorResponse(schemaId, errors.mkString("; "))
          BadRequest(content)
    }

  private def getSchema(schemaId: String) : F[Either[Response[F], String]] =
    cache.get(schemaId)
      .flatMap {
        case Some(json) => Applicative[F].pure(Right(json))
        case None => NotFound().map(Left(_))
      }


  private def deserialize(request: Request[F], onError: => JValue) : F[Either[Response[F], JValue]] =
    request.as[JValue]
      .map(Right(_) : Either[Response[F], JValue])
      .handleErrorWith {
        case e : MalformedMessageBodyFailure =>
          BadRequest(onError).map(Left(_))
      }


}
