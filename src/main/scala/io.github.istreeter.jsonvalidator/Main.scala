package io.github.istreeter.jsonvalidator

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    AppServer.stream[IO](cache = IOSchemaCache)
             .compile.drain.as(ExitCode.Success)
}
