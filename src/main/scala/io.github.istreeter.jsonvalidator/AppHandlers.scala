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

class AppHandlers[F[_] : Sync](store: SchemaStore[F])(implicit dsl: Http4sDsl[F]) {

  import dsl._

  /**
   * Handler for POSTing a new schema with a unique id
   */
  def handlePostSchema(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        schema <- EitherT(deserialize(request, Responses.forSchemaUploadUnparseable(schemaId)))
        json = JsonMethods.compact(schema)
        _ <- EitherT.right(store.put(schemaId, json))
        content = Responses.forSchemaUploadOk(schemaId)
        resp <- EitherT.right(Created(content))
      } yield resp

    eitherT.merge
  }

  /**
   * Handler for GETting an existing schema from its id
   */
  def handleGetSchema(schemaId: String) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        json <- EitherT(getSchema(schemaId))
        resp <- EitherT.right(Ok(json, `Content-Type`(MediaType.application.json)))
      } yield resp

    eitherT.merge

  }

  /**
   * Handler for validating a document against an existing schema
   */
  def handleValidation(schemaId: String, request: Request[F]) : F[Response[F]] = {

    val eitherT : EitherT[F, Response[F], Response[F]] =
      for {
        document <- EitherT(deserialize(request, Responses.forValidationUnparseable(schemaId)))
        schemaJson <- EitherT(getSchema(schemaId))
        schema = JsonMethods.parse(schemaJson)
        messages = ValidationUtils.validate(schema, document)
        resp <- EitherT.right(toValidationResponse(schemaId, messages))
      } yield resp

    eitherT.merge
  }

  /**
   * A handler for the "/" endpoint. Describes the resource.
   */
  def handleRoot() : F[Response[F]] =
    Ok(Responses.forRoot)

  /**
   * Creates a response using the result returned from ValidationUtils
   * @param schemaId Identifies the schema
   * @param messages Error messages returned by the validator. An empty iterable implies the document is valid.
   */
  private def toValidationResponse(schemaId: String, messages: Iterable[JValue]): F[Response[F]] = {
    if (messages.isEmpty) {
        val content = Responses.forValidationOk(schemaId)
        Ok(content)
    } else {
        val content = Responses.forValidationError(schemaId, messages)
        BadRequest(content)
    }
  }

  /**
   * Helper to fetch a schema from the store, or return a bad response on error
   */
  private def getSchema(schemaId: String) : F[Either[Response[F], String]] =
    store.get(schemaId)
      .flatMap {
        case Some(json) => Applicative[F].pure(Right(json))
        case None => NotFound().map(Left(_))
      }


  /**
   * Helper to deserialize a request, or return a bad response on error
   */
  private def deserialize(request: Request[F], onError: => JValue) : F[Either[Response[F], JValue]] =
    request.as[JValue]
      .map(Right(_) : Either[Response[F], JValue])
      .handleErrorWith {
        case e : MalformedMessageBodyFailure =>
          BadRequest(onError).map(Left(_))
      }


}
