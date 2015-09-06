package com.onedaytrip.service

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import com.onedaytrip.dao.TripDao
import com.onedaytrip.domain._
import com.onedaytrip.map.route
import com.onedaytrip.service.TripService._

import scala.collection.immutable.IndexedSeq

/**
 * Created by tomek on 8/22/15.
 */

// TODO: need to define arguments like coordinates, topic, activity etc
//object ComposeTrip {
//
//  case class GetAttractions() extends ServiceRequest
//
//  case class FilterAttractionSet() extends ServiceRequest
//
//  case class GetRestaurants() extends ServiceRequest
//
//  case class GetTransportation() extends ServiceRequest
//
//  case class Done(attr: List[Attraction]) extends ServiceRequest
//
//
//
//}

object TripService {

  case class GetTrips(request: OdtRequest) extends ServiceRequest
  case class GetAllAttractions(request: DTripRequest, range: Int = 50000)
  case class ReturnAllAttractions(request: DTripRequest,attractions:List[Attraction])
  case class GenerateAttractionGroup(request: DTripRequest,attractions:List[Attraction])
  case class ReturnAttractionsGroup(request: DTripRequest, attractionsGroup:List[List[Attraction]])
  case class GetAttractionsWithRestaurant(request: DTripRequest, attractionsGroup:List[List[Attraction]])
  case class ReturnAttractionsWithRestaurants(request: DTripRequest,attractions:List[List[Attraction]])
}

class TripService extends Actor {
  val log = Logging(context.system, "TripService")
  var originalSender :ActorRef = null

  override def receive: Actor.Receive = {
    case TripRequest(req) =>   //entrance of trip service
      originalSender = sender()
      val request: DTripRequest = DTripRequest(req)
      val attrSvc = context.actorOf(Props[AttractionService])
      attrSvc ! GetAllAttractions(request)
    case ReturnAllAttractions(request,attractions) =>
      val genSvc = context.actorOf(Props[GroupAttractionService])
      genSvc ! GenerateAttractionGroup(request,attractions)

    case ReturnAttractionsGroup(request, attractionsGroup) =>
     def selectFirstFive( group: List[List[Attraction]]) : IndexedSeq[List[Attraction]] = {
       for(i <- 0 to group.length-1 if i <5 ) yield group(i)
     }

     val selectedGroup = selectFirstFive(attractionsGroup).toList
     val attractionsSortedGroup : List[List[Attraction]] = for(attraction:List[Attraction] <- selectedGroup) yield {
       val coordinateList = route.calculateShortestOrderOfCoordinates(request.startPoint,attraction)
       for(coordinate:Coordinate <- coordinateList) yield coordinate.asInstanceOf[Attraction]
     }
      val resSvc = context.actorOf(Props[AttractionRestaurantService])
      resSvc ! GetAttractionsWithRestaurant(request,attractionsSortedGroup)

    case ReturnAttractionsWithRestaurants(request,attractionsGroup) =>
      def generateTrip(tripId : Int,attractions: List[Attraction]) :Trip = {
        var totalPrice = 0.0
        var duration :Float = 0
        for(a<-attractions) {
          totalPrice = totalPrice+a.price
          duration=duration+a.timing
        }

        Trip(tripId,duration,totalPrice,attractions)
      }

      val trips :Trips =  Trips(attractionsGroup.map{generateTrip(1,_)})
      originalSender ! trips
  }
}

class AttractionService extends Actor {
  val dao = new TripDao
  def getAllAttractionsAround(coordinate: Coordinate, topics: List[String], i: Int): List[Attraction] = {
    dao.query(coordinate, i, topics)
  }
  override def receive: Actor.Receive = {
    case GetAllAttractions(request,range) =>
    val attractions :List[Attraction] = getAllAttractionsAround(request.startPoint,request.topics,range)
      sender ! ReturnAllAttractions(request,attractions)
  }
}


class AttractionRestaurantService extends Actor {
  val range = 500 //around 500 meters
  def findRestaurantAround(coordinate: Coordinate): List[Restaurant] = {
    //TODO find restaurant around : coordinate
    List()
  }

  override def receive: Actor.Receive = {
    case GetAttractionsWithRestaurant(request, attractionsGroup) =>
      val attractionsGroupWithRestaurants = for(attractionList:List[Attraction] <- attractionsGroup) yield {
        for(a:Attraction <- attractionList) yield {
          val res:List[Restaurant] = findRestaurantAround(a)
          Attraction(a.order,a.name,a.imageURL,a.rating,a.timing,a.price,a.attractionType,a.lat,a.lon,res)
        }
      }

      sender ! ReturnAttractionsWithRestaurants(request,attractionsGroupWithRestaurants)

  }
}

class GroupAttractionService extends Actor {
  val maxAttractionSizeForEachTrip:Int = 5
  def generateAttractionGroup(attractions: List[Attraction], combineNumber: Int):List[List[Attraction]] = {
    var result: List[List[Attraction]] = List()
    var tmpList: List[Attraction] = List()
    for(i <-0 to attractions.length-1 ) {
      if(i != 0 && i%combineNumber == 0) {
        result = result:::List(tmpList)
        tmpList = List()
      } else {
        tmpList = tmpList ::: List(attractions(i))
      }
    }
   result
  }

  override def receive: Actor.Receive = {
    case  GenerateAttractionGroup(request,attractions:List[Attraction]) =>
     val attractionSizeForEachGroup = request.activeness.name match {
        case "Low" => 3
        case "Medium" => 4
        case "High" => 5
      }
      println("attractionSizeForEachGroup :"+attractionSizeForEachGroup)
      val attractionsGroup = generateAttractionGroup(attractions,attractionSizeForEachGroup)
      sender ! ReturnAttractionsGroup(request,attractionsGroup)
  }
}
