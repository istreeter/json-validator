package io.github.istreeter.jsonvalidator

import cats.implicits._
import cats.effect.{ContextShift, IO}
import doobie.Transactor
import org.scalatest.{WordSpec, Inside, Matchers}
import java.sql.DriverManager
import scala.concurrent.ExecutionContext.Implicits.global

class JdbcSchemaStoreSpec extends WordSpec with Matchers with Inside {


  "The JdbcSchemaStore" should {
    "round trip on a new schema" in {

      implicit val cs : ContextShift[IO] = IO.contextShift(global)
      Class.forName("org.sqlite.JDBC")
      val conn = DriverManager.getConnection("jdbc:sqlite:test")
      val xa = Transactor.fromConnection[IO](conn, global)

      val store = new JdbcSchemaStore(xa)
      val schemaId = "xyz"
      val original = """{"this is": "a schema"}"""

      val io =
        for {
          _ <- store.init()
          _ <- store.put(schemaId, original)
          returned <- store.get(schemaId)
        } yield returned

      inside (io.unsafeRunSync()) {
        case Some(returned) =>
          returned shouldBe original
      }


    }
  }
}
