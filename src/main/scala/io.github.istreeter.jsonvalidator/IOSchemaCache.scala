package io.github.istreeter.jsonvalidator

import cats.effect.{ContextShift, IO}
import doobie._
import doobie.implicits._

class IOSchemaCache(implicit cs: ContextShift[IO]) extends SchemaCache[IO] {

  val xa = Transactor.fromDriverManager[IO](
        "org.sqlite.JDBC", "jdbc:sqlite:jsonvalidator.db", "", ""
    )

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

  def put(schemaId: String, schema: String): IO[Unit] =
    // The sql sugar converts this into a prepared statement;
    // I am not just dropping string literals into an expression. ;)
    sql"""INSERT OR REPLACE INTO jsonschema (name, json)
         VALUES ($schemaId, $schema)
      """
      .update
      .run
      .transact(xa)
      .map(_ => ())

  def get(schemaId: String): IO[Option[String]] =
    // The sql sugar converts this into a prepared statement;
    // I am not just dropping a string literal into the expression. ;)
    sql"""SELECT json FROM jsonschema WHERE name=$schemaId"""
      .query[String]
      .option
      .transact(xa)

}
