package io.github.istreeter.jsonvalidator

trait SchemaCache[F[_]] {

  def put(schemaId: String, schema: String): F[Unit]

  def get(schemaId: String): F[Option[String]]

}
