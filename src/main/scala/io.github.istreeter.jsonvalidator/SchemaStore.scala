package io.github.istreeter.jsonvalidator

/**
 * Trait for a store of schemas identified by a unique id
 */
trait SchemaStore[F[_]] {

  /**
   * Put a schema into the store
   * @param schemaId Unique id of the schema
   * @param schema The schema definition. This must be valid json.
   */
  def put(schemaId: String, schema: String): F[Unit]

  /**
   * Get a schema out of the store
   * @param schemaId Unique id of the schema
   * @return The schema if it exists
   */
  def get(schemaId: String): F[Option[String]]

}
