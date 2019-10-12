package io.github.istreeter.jsonvalidator

import cats.effect.{ContextShift, IO}
import doobie._
import doobie.implicits._

/**
 * Implements the SchemaStore trait by wring schemas to a sqlite database
 */
class IOSchemaStore(implicit cs: ContextShift[IO]) extends SchemaStore[IO] {

  val xa = Transactor.fromDriverManager[IO](
        "org.sqlite.JDBC", "jdbc:sqlite:jsonvalidator.db", "", ""
    )

  /**
   * Creates the database and initialises tables.
   * This method must be run before using the store
   */
  def init() : IO[Unit] =
    sql"""CREATE TABLE IF NOT EXISTS jsonschema(
      id INTEGER NOT NULL PRIMARY KEY,
      name VARCHAR NOT NULL UNIQUE,
      json VARCHAR NOT NULL
    )"""
    .update
    .run
    .transact(xa)
    .map(_ => ())

  override def put(schemaId: String, schema: String): IO[Unit] =
    // The sql sugar converts this into a prepared statement;
    // It does not drop the string literals into the expression.
    sql"""INSERT OR REPLACE INTO jsonschema (name, json)
         VALUES ($schemaId, $schema)
      """
      .update
      .run
      .transact(xa)
      .map(_ => ())

  override def get(schemaId: String): IO[Option[String]] =
    // The sql sugar converts this into a prepared statement;
    // It does not drop the string literals into the expression.
    sql"""SELECT json FROM jsonschema WHERE name=$schemaId"""
      .query[String]
      .option
      .transact(xa)

}
