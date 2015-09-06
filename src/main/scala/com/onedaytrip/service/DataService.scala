package com.onedaytrip.service

import akka.actor.{ActorRef, Actor, Props}
import akka.event.Logging
import com.mongodb.casbah.Imports._
import com.mongodb.util.JSON
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.Mongo
import com.onedaytrip.domain._

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
    val f = Future {
      val topics = topicCol.findOne("topics" $exists true )
      if(topics.isDefined) {
        log.debug("All topics: " + topics.get)
//        val obj1 = MongoDBObject("topic" -> "amenity", "subtopics" -> MongoDBList("a","b","c"))
//        val obj2 = MongoDBObject("topic" -> "park", "subtopics" -> MongoDBList("aa","bb","cc"))
//        val obj3 = MongoDBObject("topic" -> "history", "subtopics" -> MongoDBList("aaa","bbb","ccc"))
//        val list = MongoDBList(obj1, obj2, obj3)
//        MongoDBObject("topics" -> "skfhj")
        topics.get
      }
      else MongoDBObject()
    }
    import JsonImplicits._
    f onComplete {
      case Success(obj) => target ! RequestHandler.Done(Response(obj.toString))
      case Failure(e) => target ! RequestHandler.Done(Response(MongoDBObject("error" -> e).toString))
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
