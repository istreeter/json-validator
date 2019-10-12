package io.github.istreeter.jsonvalidator

import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.json4s.JValue
import org.json4s.jackson.JsonMethods
import scala.collection.JavaConverters._

object ValidationUtils {

  lazy val fac : JsonSchemaFactory = JsonSchemaFactory.byDefault()

  def validate(schema: JValue, document: JValue) : Either[Iterable[String], Unit] = {

    val report = fac.getJsonSchema {
        JsonMethods.asJsonNode(schema)
      }
      .validate {
        JsonMethods.asJsonNode(document.noNulls)
      }

    if (report.isSuccess) {
      Right(())
    } else {
      Left {
        report.asScala.map(_.getMessage)
      }
    }
  }

}
