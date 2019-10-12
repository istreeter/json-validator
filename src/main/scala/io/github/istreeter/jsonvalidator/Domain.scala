package io.github.istreeter.jsonvalidator

import org.json4s._
import org.json4s.JsonDSL._

object Domain {

  def schemaUploadResponse(schemaId: String) =
    ("action" -> "uploadSchema") ~
    ("id" -> schemaId) ~
    ("status" -> "success")

}
