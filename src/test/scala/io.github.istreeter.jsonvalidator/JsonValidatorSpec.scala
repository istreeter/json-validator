package io.github.istreeter.jsonvalidator

import cats.effect.SyncIO
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.json4s.jackson._
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods
import org.scalatest.{WordSpec, Inside, Matchers}
import scala.io.Source


class JsonValidatorSpec extends WordSpec with Matchers with Inside {

  import JsonValidatorSpec._
  implicit lazy val formats = DefaultFormats

  "The JsonValidator app" when {
    "store is empty" should {

      "accept new schemas" in {

        val store = TestSchemaStore.empty
        val routes = AppRoutes[SyncIO].routes(store)
        val schema = loadConfigSchema()
        val schemaId = "xyz"

        val response = postSchema(routes, schemaId, schema)

        inside(response) {
          case Some(r) =>
            r.status shouldBe Status.Created

            val body = r.as[JValue].unsafeRunSync
            (body \ "status").extract[String] shouldBe "success"
            (body \ "id").extract[String] shouldBe schemaId

        }

        store.contents.keys should contain (schemaId)

      }

      "reject bad schemas" in {
        val store = TestSchemaStore.empty
        val routes = AppRoutes[SyncIO].routes(store)
        val schemaId = "xyz"

        val schema = "bad { json ]"

        val response = postSchema(routes, schemaId, schema)

        inside(response) {
          case Some(r) =>
            r.status shouldBe Status.BadRequest

            val body = r.as[JValue].unsafeRunSync
            (body \ "status").extract[String] shouldBe "error"
            (body \ "id").extract[String] shouldBe schemaId
        }

        store.contents shouldBe empty

      }
    }

    "store contains one schema" should {

      "return the schema" in {
        val schema = loadConfigSchema()
        val schemaId = "xyz"
        val store = TestSchemaStore.of(schemaId -> schema)
        val routes = AppRoutes[SyncIO].routes(store)

        val response = getSchema(routes, schemaId)

        inside(response) {
          case Some(r) =>
            r.status shouldBe Status.Ok

            val body : JValue = r.as[JValue].unsafeRunSync
            val original : JValue = JsonMethods.parse(schema)

            body shouldEqual original
        }
      }

      "validate a valid document" in {

        val schema = loadConfigSchema()
        val document = loadConfigDocument()
        val schemaId = "xyz"
        val store = TestSchemaStore.of(schemaId -> schema)
        val routes = AppRoutes[SyncIO].routes(store)

        val response = postToValidate(routes, schemaId, document)

        inside(response) {
          case Some(r) =>
            r.status shouldBe Status.Ok

            val body = r.as[JValue].unsafeRunSync
            (body \ "status").extract[String] shouldBe "success"
            (body \ "id").extract[String] shouldBe schemaId
        }

      }

      "reject an invalid document" in {

        val schema = loadConfigSchema()
        val document = loadBadDocument()
        val schemaId = "xyz"
        val store = TestSchemaStore.of(schemaId -> schema)
        val routes = AppRoutes[SyncIO].routes(store)

        val response = postToValidate(routes, schemaId, document)

        inside(response) {
          case Some(r) =>
            r.status shouldBe Status.BadRequest

            val body = r.as[JValue].unsafeRunSync
            (body \ "status").extract[String] shouldBe "error"
            (body \ "id").extract[String] shouldBe schemaId
        }

      }

    }



  }

}

object JsonValidatorSpec {

  def loadResource(path: String): String =
    Source.fromURL(getClass.getResource(path)).mkString

  def loadConfigSchema(): String = loadResource("/config-schema.json")
  def loadConfigDocument(): String = loadResource("/config-document.json")
  def loadBadDocument(): String = loadResource("/bad-config-document.json")

  def schemaUri(schemaId: String) : Uri =
    Uri.uri("/schema") / schemaId

  def validateUri(schemaId: String) : Uri =
    Uri.uri("/validate") / schemaId

  def postSchema(routes: HttpRoutes[SyncIO], schemaId : String, schema: String) : Option[Response[SyncIO]] =
      routes
        .run {
          Request(method = Method.POST, uri = schemaUri(schemaId)).withEntity(schema)
        }
        .value
        .unsafeRunSync()

  def getSchema(routes: HttpRoutes[SyncIO], schemaId : String) : Option[Response[SyncIO]] =
      routes
        .run {
          Request(method = Method.GET, uri = schemaUri(schemaId))
        }
        .value
        .unsafeRunSync()

  def postToValidate(routes: HttpRoutes[SyncIO], schemaId : String, document: String) : Option[Response[SyncIO]] =
      routes
        .run {
          Request(method = Method.POST, uri = validateUri(schemaId)).withEntity(document)
        }
        .value
        .unsafeRunSync()
}
