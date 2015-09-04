package com.onedaytrip

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.onedaytrip.config.Configuration
import spray.can.Http

import scala.concurrent.duration._
import scala.util.Properties

/**
 * Created by tomek on 6/7/15.
 */
object Boot extends App with Configuration {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[OdtServiceActor], "onedaytrip-service")

  val vport = Properties.envOrElse("PORT", "8982").toInt
  println("Starting on port: "+vport)

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = vport)
}
