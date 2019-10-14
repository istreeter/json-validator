package io.github.istreeter.jsonvalidator

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]) = {

    val store = JdbcSchemaStore()

    val server =
      AppServer.stream[IO](store).compile.drain

    for {
      _ <- store.init()
      _ <- server
    } yield ExitCode.Success

  }

}
