name := """ride"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.12.6"
scalaVersion := "2.11.12"

libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.1"
libraryDependencies += jdbc
libraryDependencies += ehcache
libraryDependencies += ws
libraryDependencies += "org.json" % "json" % "20180130"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0"
libraryDependencies += "com.google.apis" % "google-api-services-gmail" % "v1-rev83-1.23.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.4"
//libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.3"
//libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.2.3"
//libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
//libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_joda-time" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_jts" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_json4s" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_play-json" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_spray-json" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_argonaut" % "0.16.2"
//libraryDependencies += "com.github.tminglei" %% "slick-pg_circe-json" % "0.16.2"

//libraryDependencies += "io.getquill" %% "quill" % "2.5.4"
//libraryDependencies +=  "io.getquill" %% "quill-jdbc" % "2.5.4"
libraryDependencies +=  "io.getquill" %% "quill-async-postgres" % "2.5.4"
//libraryDependencies +=  "io.getquill" %% "quill-async-postgres" % "1.1.0"

