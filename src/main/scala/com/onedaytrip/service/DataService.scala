package com.onedaytrip.service

import akka.actor.Actor
import com.mongodb.casbah.{MongoCursor, MongoClient}
import com.onedaytrip.config.Configuration
import com.onedaytrip.db.Mongo

/**
 * Created by tomek on 9/3/15.
 */

class QueryService extends Actor with Configuration {

  lazy val mongo = Mongo(MongoClient(), mongoDb.toString)
  def poiCol = mongo.collection(poiCollection.toString)
  def topicCol = mongo.collection(topicCollection.toString)

  override def receive: Receive = ???
}

object DataService {
  case class PoiQuery()
  case class TopicQuery()
  case class Done(cursor:MongoCursor)
}

class DataService extends Actor with RequestHandler {

  def waiting: Receive = {
    /**
     * case Auth(req) =>
      context.become(runNext(Vector(Job(sender, Props[AuthSvc], req))))
     */
  }
}
