package com.onedaytrip.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import com.mongodb.casbah.{MongoCursor, MongoClient}
import com.onedaytrip.ServiceRequest
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.Mongo
import com.onedaytrip.domain.{Topic, OdtRequest, NotImplementedYet}
import com.onedaytrip.service.DataService.{TopicQuery, PoiQuery}

/**
 * Created by tomek on 9/3/15.
 */

class QueryService extends Actor with Configuration {
  val log = Logging(context.system, "QueryService")
  lazy val mongo = Mongo(MongoClient(), mongoDb.toString)
  def poiCol = mongo.collection(poiCollection.toString)
  def topicCol = mongo.collection(topicCollection.toString)

  override def receive: Receive = {
    case Topic(_,_) => {
      log.debug("PoiQUery done")
      sender ! RequestHandler.Done(NotImplementedYet("PoiQuery - done!"))
    }
  }
}

object DataService {
  case class PoiQuery()
  case class TopicQuery(odtRequest: OdtRequest) extends ServiceRequest
  case class Done(cursor:MongoCursor)
}

class DataService extends Actor with RequestHandler {

  override def waiting: Receive = {
     case TopicQuery(req) => {
       log.debug("DataService")
       context.become(runNext(Vector(Job(sender, Props[QueryService], req))))
     }
  }
}
