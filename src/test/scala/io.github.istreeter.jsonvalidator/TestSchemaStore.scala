package io.github.istreeter.jsonvalidator

import cats.effect.SyncIO
import scala.collection.mutable.Map

class TestSchemaStore(val contents: Map[String, String]) extends SchemaStore[SyncIO] {

  override def put(schemaId: String, schema: String): SyncIO[Unit] =
    SyncIO {
      contents += (schemaId -> schema)
      ()
    }

  override def get(schemaId: String): SyncIO[Option[String]] =
    SyncIO {
      contents.get(schemaId)
    }

}

object TestSchemaStore {

  def empty: TestSchemaStore = new TestSchemaStore(Map.empty)

  def of(elems: (String, String)*) = new TestSchemaStore(Map(elems : _*))

}
