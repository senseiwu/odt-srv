package com.onedaytrip

import akka.actor.{Actor, ActorRefFactory, Props}
import akka.event.slf4j.SLF4JLogging
import com.onedaytrip.PerRequest.{DataRequestHandler, TripRequestHandler}
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.SillyDbPopulator
import com.onedaytrip.domain._
import com.onedaytrip.service.{DataService, StatisticService, TripService}
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.routing.HttpService

/**
 * Created by tomek on 6/7/15.
 */

class OdtServiceActor extends Actor with OdtService with Configuration {
  def receive = runRoute(routing)
  def actorRefFactory: ActorRefFactory = context
}

trait OdtService extends HttpService with SLF4JLogging {

  this: Actor =>

  val tripCreator = context.actorOf(Props[TripService])
  val statisticSvc = context.actorOf(Props[StatisticService])
  val dataSvc = context.actorOf(Props[DataService])

  val dbpop = new SillyDbPopulator

  val appRoute = respondWithMediaType(MediaTypes.`application/json`) {
    import JsonImplicits._
    get {
      path("version") {
        complete(Version(version.toString))
      }
    }
  }

  val topicRoute = respondWithMediaType(MediaTypes.`application/json`) {
    pathPrefix("topic") {
      path("all") {
        ctx => context.actorOf(Props(new DataRequestHandler(ctx, List(dataSvc), TopicRequest())))
      }
    }
  }

  val tripRoute = respondWithMediaType(MediaTypes.`application/json`) {
    pathPrefix("trips") {
      parameters('startPointName.as[String] ?, 'coordinates.as[String] ?, 'activeness.as[String] ?,
        'topics.as[String] ?, 'budget.as[String] ?).as(TripRequestParams) {
        tripReq:TripRequestParams => {
          ctx => context.actorOf(Props(new TripRequestHandler(ctx, List(tripCreator), tripReq)))
        }
      }
    }
  }

  val poiRoute = {
    import JsonImplicits._
    pathPrefix("poi") {
      pathPrefix("country" / Segment) {
        country => pathEnd {
          complete(NotImplementedYet("poi/country/"+country))
        }
      } ~ path("country") {
        pathEnd {
          complete(NotImplementedYet("poi/country/"))
        }
      }
    }
  }

  val routing = topicRoute ~ poiRoute ~ tripRoute ~ appRoute

}
