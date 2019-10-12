package io.github.istreeter.jsonvalidator

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    JsvonvalidatorServer.stream[IO].compile.drain.as(ExitCode.Success)
}
