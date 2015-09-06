package com.onedaytrip.service

import akka.actor.{ActorRef, Actor, Props}
import akka.event.Logging
import com.mongodb.casbah.Imports._
import com.mongodb.util.JSON
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.Mongo
import com.onedaytrip.domain._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by tomek on 9/3/15.
 */

class QueryService extends Actor with Configuration {
  val log = Logging(context.system, "QueryService")
  lazy val mongo = Mongo(MongoClient(), mongoDb.get)
  def poiCol = mongo.collection(poiCollection.get)
  def topicCol = mongo.collection(topicCollection.get)

  override def receive: Receive = {
    case TopicRequest() => queryAll(sender)
    case TopicMapRequest() => sender ! ???
    case TopicSubtopicRequest(topic) => sender ! ???
  }

  def queryAll(target:ActorRef) = {
    lazy val f = Future {
      TopicResponse(topicCol.find())
    }

    f onComplete {
      case Success(obj:TopicResponse) => target ! RequestHandler.Done(obj)
      case Failure(e) => target ! RequestHandler.Done(TopicResponse())
    }
  }
}

object DataService {
  case class Done(cursor:MongoCursor)
}

class DataService extends Actor with RequestHandler {
  override def waiting: Receive = {
     case DataRequest(req) => {
       log.debug("DataService request received - " + req)
       context.become(runNext(Vector(Job(sender, Props[QueryService], req))))
     }
     case _ => log.error("")
  }
}
