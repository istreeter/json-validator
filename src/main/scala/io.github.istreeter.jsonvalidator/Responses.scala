package io.github.istreeter.jsonvalidator

import org.json4s.JsonDSL._
import org.json4s.JValue
import org.json4s.JsonAST.JObject

object Responses {

  implicit class ResponseOps(val obj: JObject) extends AnyVal {

    def withAction(action: String): JObject =
      obj ~ ("action" -> action)

    def withStatus(status: String): JObject =
      obj ~ ("status" -> status)

    def withMessage(message: String): JObject =
      obj ~ ("message" -> message)

  }


  def idResponse(schemaId: String) : JObject =
    ("id" -> schemaId)

  def schemaUploadResponse(schemaId: String) : JValue =
    idResponse(schemaId)
      .withAction("uploadSchema")
      .withStatus("success")

  def schemaUploadErrorResponse(schemaId: String) : JValue =
    idResponse(schemaId)
      .withAction("uploadSchema")
      .withStatus("error")
      .withMessage("Invalid JSON")

  def validationOkResponse(schemaId: String) : JValue =
    idResponse(schemaId)
      .withAction("validateDocument")
      .withStatus("success")

  def validationErrorResponse(schemaId: String, msg: String) : JValue =
    idResponse(schemaId)
      .withAction("validateDocument")
      .withStatus("error")
      .withMessage(msg)

}
