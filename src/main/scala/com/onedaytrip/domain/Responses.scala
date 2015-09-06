package com.onedaytrip.domain

import com.mongodb.casbah.Imports._
import spray.json.DefaultJsonProtocol

import scala.collection.mutable.ArrayBuffer

/**
 * Created by tomek on 7/7/15.
 */
object UserStatus {
  val Success = 0
  val Failure = -1
  val UserExists = -2
  val PasswordNotMatch = -3
  val UserNotFound = -4
  val UnknownError = -99
}

object Attraction {
  val History = "HISTORY"
  val Entertainment = "ENTERTAINMENT"
  val Park = "PARK"
  val Restaurant = "RESTAURANT"
  val Shopping = "SHOPPING"
}


// Responses
trait OdtResponse
//case class Response(mongoObj:String) extends OdtResponse
case class Trips(trips:List[Trip]) extends OdtResponse

object Point {
  val ATTRACTION_TYPE = 1
  val TRAFFIC_STATION = 2
  val RESTAURANT = 3
}
abstract class Point(pointType:Int, lat: Double, lon: Double) extends Coordinate(lat,lon)

case class Trip(id:Int, duration:Float, totalCost:Double, points:List[Point])
case class Attraction(order:Int, name:String, imageURL:String, rating:Int, timing:Float, price:Int, attractionType:String, latParam: Double,lonParam: Double ,restaurants:List[Restaurant] = null  ) extends Point(Point.ATTRACTION_TYPE, latParam,lonParam)
case class TrafficLine(name:String, fromPoint:Point, toPoint:Point , cost:Float)
case class TrafficStation(name:String, latParam: Double, lonParam: Double) extends Point(Point.TRAFFIC_STATION,latParam,lonParam)

class Coordinate(latParam:Double, lonParam:Double) {
  var lat = latParam
  var lon = lonParam
}

object Coordinate {
  def apply(lat:String, lon:String): Coordinate = new Coordinate(lat.toDouble, lon.toDouble)
  def apply(param: String): Coordinate =  param.split(",").toList match {
    case head :: tail => new Coordinate(head.toDouble, tail.head.toDouble)
  }
}

case class Version(v:String)
case class NotImplementedYet(msg:String) extends OdtResponse
case class Topics(topics:List[String])

case class TopicItem(topic:String, subtopics:List[String])
case class TopicResponse(topics:List[TopicItem]) extends OdtResponse

object TopicResponse {
  def apply():TopicResponse = new TopicResponse(List())
  def apply(topics:MongoCursor):TopicResponse = {
    val tt = ArrayBuffer[TopicItem]()
    for(topic <- topics) {
      val tts = ArrayBuffer[String]()
      val name = topic.getAs[String]("topic")
      val subjects = topic.get("subtopics")
      for(subject <- subjects.asInstanceOf[BasicDBList]) {
        tts += subject.toString
      }
      tt += TopicItem(name.get, tts.toList)
    }
    new TopicResponse(tt.toList)
  }
}

object JsonImplicits extends DefaultJsonProtocol {
  implicit val topics = jsonFormat1(Topics)
  implicit val version = jsonFormat1(Version)
  implicit val niy = jsonFormat1(NotImplementedYet)
  //implicit val response = jsonFormat1(Response)
}

class Restaurant(name:String, eachPersonCost:Double , coordinate: Coordinate,lat: Double, lon: Double) extends Point(3,lat,lon)
