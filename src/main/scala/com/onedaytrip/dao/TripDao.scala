package com.onedaytrip.dao

import akka.actor.Actor
import akka.event.Logging
import com.mongodb.casbah.Imports._

import com.onedaytrip.db.Mongo
import com.onedaytrip.domain._
import com.onedaytrip.service.TripService.GetTrips


/**
 * Created by tomek on 6/7/15.
 */



class TripDao {

  val mongo = Mongo(MongoClient(), "poi")
  val db = mongo.getdb
  val col = mongo.collection("common")
  col.createIndex(MongoDBObject("loc" -> "2d"))

  def query(loc:Coordinate, range:Int, topics:List[Topic]): List[Attraction] = {
    def queryTopic(loc:Coordinate, range:Int, topics:List[Topic]): List[List[Attraction]] = topics match {
      case List() => Nil
      case head :: tail =>
        println("TOPIC: " + head.name)
        val ll = parse(mongo.findNear(col, head.name, loc.lat, loc.lon, range))
        ll :: queryTopic(loc, range, tail)
    }


    if(topics.isEmpty) parse(mongo.findNear(col, loc.lat, loc.lon, range))
    else {
      val attr = queryTopic(loc, range, topics).flatMap(x=>x)
      printf("Attr: " + attr)
      attr
    }
  }

  def parse(obj:MongoCursor):List[Attraction] = {

    def getItem(key:String, obj:DBObject) = {
      val value = obj.get(key)
      if (value == null) ""
      else value.toString
    }

    def getItemIx(key:Int, obj:BasicDBList) = obj.get(key).toString

    def getLoc(obj:DBObject):Coordinate = {
      val value:BasicDBList = obj.get("loc").asInstanceOf[BasicDBList]
      if(value == null) Coordinate(",")
      else Coordinate(getItemIx(0, value), getItemIx(1, value))
    }

    var ll:List[Attraction] = List()
    var i = 0
    while(obj.hasNext) {
      val item = obj.next()
      println("item: " + item)
      val loc:BasicDBList = item.get("loc").asInstanceOf[BasicDBList]
      val lat = loc.get(0).toString
      val lon = loc.get(1).toString
      ll =
        Attraction(
          i,
          getItem("name", item),
          getItem("img", item),
          0,
          0,
          0,
          getItem("subtype", item),
          lat.toDouble,
          lon.toDouble) :: ll
      i = i + 1
    }
    ll
  }
//case class Attraction(
// order:Int,
// name:String,
// imageURL:String,
// rating:Int,
// timing:Float,
// price:Int,
// attractionType:String,
// latParam: Double,
// lonParam: Double ,
// restaurants:List[Restaurant] = null  ) extends Point(Point.ATTRACTION_TYPE, latParam,lonParam)
  def createTrips(attr:List[Attraction]):Trips = {
    Trips(List(Trip(1,0,0,attr)))
  }

}

