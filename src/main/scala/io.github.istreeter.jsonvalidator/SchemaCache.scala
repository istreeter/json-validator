package io.github.istreeter.jsonvalidator

import org.json4s.JValue

trait SchemaCache[F[_]] {

  def putSchema(schemaId: String, schema: JValue): F[Unit]

  def getSchema(schemaId: String): F[Option[JValue]]

}
