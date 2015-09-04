package com.onedaytrip.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.mongodb.casbah.{MongoClient, MongoCursor}
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.Mongo
import com.onedaytrip.domain._

/**
 * Created by tomek on 9/3/15.
 */

class QueryService extends Actor with Configuration {
  val log = Logging(context.system, "QueryService")
  lazy val mongo = Mongo(MongoClient(), mongoDb.toString)
  def poiCol = mongo.collection(poiCollection.toString)
  def topicCol = mongo.collection(topicCollection.toString)

  override def receive: Receive = {
    case TopicRequest() => {
      log.debug("Topic request done")
      sender ! RequestHandler.Done(NotImplementedYet("Topic - done!"))
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
