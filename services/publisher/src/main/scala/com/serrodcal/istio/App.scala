package com.serrodcal.istio

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._

object App extends App {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem("publisher-actor-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  implicit val logger = Logging(system, getClass)

  val route =
    path("publish") {
      get {
        parameter("helloStr".as[String]) { helloStr =>
          logger.info(s"helloStr '${helloStr}' received!")
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, helloStr))
        }
      }
    }

  val liveness = path("liveness"){
    get {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Alive!"))
    }
  }

  val interface = config.getString("app.interface")
  val port = config.getString("app.port")

  val bindingFuture = Http().bindAndHandle(route~liveness, interface, port.toInt)

  logger.info(s"Server online at http://${interface}:${port}/")
}
