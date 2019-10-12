package io.github.istreeter.jsonvalidator

import org.json4s.JsonDSL._
import org.json4s.{JValue, JsonAST}

object Responses {

  def baseResponse(schemaId: String, action: String, status: String) : JsonAST.JObject =
    ("action" -> action) ~
    ("id" -> schemaId) ~
    ("status" -> status)

  def schemaUploadResponse(schemaId: String) : JValue =
    baseResponse(schemaId, "uploadSchema", "success")

  def schemaUploadErrorResponse(schemaId: String) : JValue =
    baseResponse(schemaId, "uploadSchema", "error") ~
    ("message" -> "Invalid JSON")

}
