package io.github.istreeter.jsonvalidator

import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.json4s.JValue
import org.json4s.jackson.JsonMethods
import scala.collection.JavaConverters._

object ValidationUtils {

  lazy val fac : JsonSchemaFactory = JsonSchemaFactory.byDefault()

  /**
   * Validates a json document against a schema
   * @param schema The schema against which to validate
   * @param document The document to be validated
   * @return An iterable describing any errors. An empty iterable implies the document is valid
   */
  def validate(schema: JValue, document: JValue) : Iterable[JValue] =
    fac.getJsonSchema {
        JsonMethods.asJsonNode(schema)
      }
      .validate {
        JsonMethods.asJsonNode(document.noNulls)
      }
      .asScala
      .map(_.asJson)
      .map(JsonMethods.fromJsonNode)

}
