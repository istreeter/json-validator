val Http4sVersion = "0.20.8"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.istreeter",
    name := "jsonvalidator",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",

    fork in run := true,

    libraryDependencies ++= Seq(
      "org.http4s"                 %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"                 %% "http4s-blaze-client"    % Http4sVersion,
      "org.http4s"                 %% "http4s-dsl"             % Http4sVersion,
      "org.http4s"                 %% "http4s-json4s-jackson"  % Http4sVersion,
      "org.xerial"                 %  "sqlite-jdbc"            % "3.28.0",
      "org.tpolecat"               %% "doobie-core"            % "0.7.1",
      "com.github.java-json-tools" %  "json-schema-validator"  % "2.2.11",
      "org.specs2"                 %% "specs2-core"            % Specs2Version % "test",
      "org.specs2"                 %% "specs2-core"            % Specs2Version % "test",
      "ch.qos.logback"             %  "logback-classic"        % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
