package com.serrodcal.istio

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._
import scalaj.http.HttpResponse

object App extends App {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem("publisher-actor-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  implicit val logger = Logging(system, getClass)

  val formatterInterface = config.getString("formatter.interface")
  val formatterPort = config.getString("formatter.port")
  val publisherInterface = config.getString("publisher.interface")
  val publisherPort = config.getString("publisher.port")

  val route =
    path("hello") {
      get {
        parameter("helloTo".as[String], "greeting".as[String]) { (helloTo, greeting) =>
          logger.info(s"helloTo ${helloTo} and greeting ${greeting} received!")
          val formatterResponse: HttpResponse[String] = scalaj.http.Http(s"http://${formatterInterface}:${formatterPort}/format").param("helloTo",s"${helloTo}").param("greeting",s"${greeting}").asString
          if (formatterResponse.code == 200) {
            val helloStr = formatterResponse.body
            logger.info(s"call to formatter successful with '${helloStr}'")
            val publisherResponse: HttpResponse[String] = scalaj.http.Http(s"http://${publisherInterface}:${publisherPort}/publish").param("helloStr",s"${helloStr}").asString
            if (publisherResponse.code == 200) {
              val response = publisherResponse.body
              logger.info(s"call to publisher successful with '${response}'")
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, response))
            } else {
              logger.error(s"Error calling publisher with sc ${publisherResponse.code}")
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Some error in publisher with sc ${publisherResponse.code}"))
            }
          } else {
            logger.error(s"Error calling formatter with sc ${formatterResponse.code}")
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Some error in formatter with sc ${formatterResponse.code}"))
          }
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
