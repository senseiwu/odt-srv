package com.onedaytrip

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}
import akka.event.slf4j.SLF4JLogging
import akka.util.Timeout
import com.onedaytrip.PerRequest.{TripRequest, AuthRequest}
import com.onedaytrip.db.poi.{shop, leisure, amenity}
import com.onedaytrip.db.{SillyDbPopulator, Db}
import com.onedaytrip.domain._
import com.onedaytrip.service.{DataService, TripService, StatisticService, UserService}
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, Route}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by tomek on 6/7/15.
 */

class OdtServiceActor extends Actor with OdtService {
  //def receive = runRoute(indexPageRoute ~ route)
  def receive = runRoute(routing)
  def actorRefFactory: ActorRefFactory = context

//  @throws[Exception](classOf[Exception])
//  override def postStop(): Unit =
//    println("Good bye!")
//    DriverManager.getDrivers.asScala.foreach {d => DriverManager.deregisterDriver(d)}
}

trait OdtService extends HttpService with SLF4JLogging {

  this: Actor =>

//  val setupFuture = Db.create
//  val result = setupFuture.andThen{case _ => Db.close}
//  Await.result(result, Timeout(2 seconds).duration)

  val tripCreator = context.actorOf(Props[TripService])
  val userAuth = context.actorOf(Props[UserService])
  val statisticSvc = context.actorOf(Props[StatisticService])
  val dataSvc = context.actorOf(Props[DataService])

  val dbpop = new SillyDbPopulator

  //val indexPageRoute: Route = pathPrefix("") { getFromDirectory("src/main/webapp") }

  val topicRoute = respondWithMediaType(MediaTypes.`application/json`) {
    import JsonImplicits._
    pathPrefix("topic") {
      path("all") {
        ctx => context.actorOf(Props(new TripRequest(ctx, List(dataSvc), Topic(1, "shop"))))
        //complete(NotImplementedYet("all topics NA"))
      } ~ path("subtopics") {
        complete(NotImplementedYet("subtopics NA"))
      } ~ path("map") {
        complete(NotImplementedYet("map not ready yet"))
      }

    }
  }

  val tripRoute = respondWithMediaType(MediaTypes.`application/json`) {
    import JsonImplicits._
    pathPrefix("trips") {
      parameters('startPointName.as[String] ?, 'coordinates.as[String] ?, 'activeness.as[String] ?,
        'topics.as[String] ?, 'budget.as[String] ?).as(TripRequestParams) {
        tripReq:TripRequestParams => {
          ctx => context.actorOf(Props(new TripRequest(ctx, List(tripCreator), tripReq)))
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
          complete("kjsdfh")
        }
      }
    }
  }

  val userRoute = respondWithMediaType(MediaTypes.`application/json`) {
    import JsonImplicits._
    pathPrefix("user") {
      path("login") {
        complete(NotImplementedYet("user login NA"))
      } ~
        path("signin") {
          complete(NotImplementedYet("user signin NA"))
        }
    } ~
      pathPrefix("user" / IntNumber) { uid => {
        path("trips") {
          complete(NotImplementedYet("user " + uid + " trips NA"))
        } ~ path("contacts") {
          complete(NotImplementedYet("user " + uid + " contacts NA"))
        }
      }
      }
  }

  val routing = topicRoute ~ poiRoute

  val route = respondWithMediaType(MediaTypes.`application/json`) {
    import JsonImplicits._
    get {
      path("version") {
        complete(Version("1.0.0"))
      } ~
        path("topics") {
          complete(Topics(List(leisure.ValPark, shop.Key)))
        } ~
        path("trips") {
          parameters('startPointName.as[String] ?, 'coordinates.as[String] ?, 'activeness.as[String] ?,
            'topics.as[String] ?, 'budget.as[String] ?).as(TripRequestParams) {
            tripReq: TripRequestParams => {
              ctx => context.actorOf(Props(new TripRequest(ctx, List(tripCreator), tripReq)))
            }
          }
        }
    }

  }

  def reqstHndlr(message: OdtRequest, actor:ActorRef):Route =
    //ctx => context.actorOf(Props(new PerRequest(ctx, actor, message)))
    ctx => context.actorOf(Props(new AuthRequest(ctx, List(userAuth), message)))

}

//      ~
//      path("login") {
//        parameters('name.as[String] ?, 'password.as[String] ?).as(UserLogin) {
//          user:UserLogin => {
//            ctx => context.actorOf(Props(new AuthRequest(ctx, List(userAuth, statisticSvc), user)))
//          }
//        }
//      } ~
//      path("signin") {
//        parameters('name.as[String] ?, 'password1.as[String] ?, 'password2.as[String] ?).as(UserSignIn) {
//          user:UserSignIn => {
//            ctx => context.actorOf(Props(new AuthRequest(ctx, List(userAuth), user)))
//          }
//        }
//      } ~
//      path("chat") {
//        parameter('uid.as[Int] ?, 'text.as[String] ?).as(Chat) {
//          chat:Chat => reqstHndlr(chat, userAuth)
//        }
//      } ~
//      path("search") {
//        parameter('gender.as[Int] ?, 'age.as[Int] ?).as(SearchUsers) {
//          search:SearchUsers => reqstHndlr(search, userAuth)
//        }
//      }
//    } ~
//    post {
//      path("loginasync") {
//        entity(as[UserLogin]) {
//          user:UserLogin => reqstHndlr(user, userAuth)
//        }
//      }
//    }