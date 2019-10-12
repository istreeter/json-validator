package io.github.istreeter.jsonvalidator

import org.json4s.JsonDSL._
import org.json4s.{JValue, JArray, JObject}

object Responses {

  implicit class ResponseOps(val obj: JObject) extends AnyVal {

    def withAction(action: String): JObject =
      obj ~ ("action" -> action)

    def withStatus(status: String): JObject =
      obj ~ ("status" -> status)

    def withId(id: String): JObject =
      obj ~ ("id" -> id)

    def withMessage(message: JValue): JObject =
      obj ~ ("message" -> message)

  }

  val Success = "success"
  val Error = "error"
  val UploadSchema = "uploadSchema"
  val ValidateDocument = "validateDocument"
  val InvalidJson = "Invalid JSON"


  def forSchemaUploadOk(schemaId: String) : JValue =
    JObject()
      .withId(schemaId)
      .withAction(UploadSchema)
      .withStatus(Success)

  def forSchemaUploadUnparseable(schemaId: String) : JValue =
    JObject()
      .withId(schemaId)
      .withAction(UploadSchema)
      .withStatus(Error)
      .withMessage(InvalidJson)

  def forValidationOk(schemaId: String) : JValue =
    JObject()
      .withId(schemaId)
      .withAction(ValidateDocument)
      .withStatus(Success)

  def forValidationError(schemaId: String, messages: Iterable[JValue]) : JValue =
    JObject()
      .withId(schemaId)
      .withAction(ValidateDocument)
      .withStatus(Error)
      .withMessage(messages)

  def forValidationUnparseable(schemaId: String) : JValue =
    forValidationError(schemaId, List(("message" -> InvalidJson)))

}
