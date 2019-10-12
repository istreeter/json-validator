package io.github.istreeter.jsonvalidator

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]) = {

    val cache = new IOSchemaStore

    val server =
      AppServer.stream[IO](cache).compile.drain

    for {
      _ <- cache.init()
      _ <- server
    } yield ExitCode.Success

  }

}
