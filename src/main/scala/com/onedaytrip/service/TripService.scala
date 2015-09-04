package com.onedaytrip.service

import akka.actor.{ActorRef, Actor, Props}
import akka.event.Logging
import com.onedaytrip.ServiceRequest
import com.onedaytrip.dao.TripDao
import com.onedaytrip.domain._
import com.onedaytrip.map.route
import com.onedaytrip.service.ComposeTrip.Done
import com.onedaytrip.service.TripService._
import scala.collection.immutable.IndexedSeq
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by tomek on 8/22/15.
 */

// TODO: need to define arguments like coordinates, topic, activity etc
object ComposeTrip {

  case class GetAttractions() extends ServiceRequest

  case class FilterAttractionSet() extends ServiceRequest

  case class GetRestaurants() extends ServiceRequest

  case class GetTransportation() extends ServiceRequest

  case class Done(attr: List[Attraction]) extends ServiceRequest



}

class QueryForAttractions extends Actor {
  val log = Logging(context.system, "QueryForAttractions")

  override def receive: Receive = {
    case ComposeTrip.GetAttractions() =>
      log.debug("get attraction from db")
      sender ! ComposeTrip.Done(List())
  }
}

class ComposeTrip extends Actor with RequestHandler {
  //  val log = Logging(context.system, "ComposeTrip")
  //  // TODO: here implement logic to compose trip, like using/sending msg to another actor ...
  //  override def receive:Receive = {
  //    case TripRequestParams(startPoint,coordinates,activeness,topics,budget) =>
  //      log.debug("Composer trip stared work")
  //      context.become(runNext(Vector(Job(sender, Props[ComposeTrip], req))))
  //    case ComposeTrip.Done(_) =>
  //      sender ! RequestHandler.Done(Trips(List()))
  //  }
}



object TripService {

  case class GetTrips(request: OdtRequest) extends ServiceRequest
  case class GetAllAttractions(request: DTripRequest, range: Int = 50000)
  case class ReturnAllAttractions(request: DTripRequest,attractions:List[Attraction])
  case class GenerateAttractionGroup(request: DTripRequest,attractions:List[Attraction])
  case class ReturnAttractionsGroup(request: DTripRequest, attractionsGroup:List[List[Attraction]])
  case class GetAttractionsWithRestaurant(request: DTripRequest, attractionsGroup:List[List[Attraction]])
  case class ReturnAttractionsWithRestaurants(request: DTripRequest,attractions:List[List[Attraction]])
  //case class FillArractions()
}

class TripService extends Actor {
  val log = Logging(context.system, "TripService")
  var originalSender :ActorRef = null

  override def receive: Actor.Receive = {
    case GetTrips(req) =>   //entrance of trip service
      originalSender = sender()
      log.debug("Receive GetTrip Request " + req)
      println("start ----> GetTrips ")
      val request: DTripRequest = DTripRequest(req)
      println("GetTrips <- during11")
      val attrSvc = context.actorOf(Props[AttractionService])
      println("GetTrips <- end")
      attrSvc ! GetAllAttractions(request)
    case ReturnAllAttractions(request,attractions) =>
      println("start -> ReturnAllAttractions ")
      val genSvc = context.actorOf(Props[GroupAttractionService])
      println("ReturnAllAttractions <- end:"+attractions.length)
      genSvc ! GenerateAttractionGroup(request,attractions)

    case ReturnAttractionsGroup(request, attractionsGroup) =>
      println("start -> ReturnAttractionsGroup :attractionsGroup:"+attractionsGroup.length)
     def selectFirstFive( group: List[List[Attraction]]) : IndexedSeq[List[Attraction]] = {
       for(i <- 0 to group.length-1 if i <5 ) yield group(i)
     }

     val selectedGroup = selectFirstFive(attractionsGroup).toList
     val attractionsSortedGroup : List[List[Attraction]] = for(attraction:List[Attraction] <- selectedGroup) yield {
       val coordinateList = route.calculateShortestOrderOfCoordinates(request.startPoint,attraction)
       for(coordinate:Coordinate <- coordinateList) yield coordinate.asInstanceOf[Attraction]
     }
      println("ReturnAttractionsGroup <- end:attractionsSortedGroup:"+attractionsSortedGroup.length)
      val resSvc = context.actorOf(Props[AttractionRestaurantService])
      resSvc ! GetAttractionsWithRestaurant(request,attractionsSortedGroup)

    case ReturnAttractionsWithRestaurants(request,attractionsGroup) =>
      println("start -> ReturnAttractionsWithRestaurants :attractionsGroup:"+attractionsGroup.length)
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
      println("ReturnAttractionsWithRestaurants <- end::::"+trips.trips.length)
      originalSender ! trips
  }
}

class AttractionService extends Actor {

  val dao = new TripDao

  def getAllAttractionsAround(coordinate: Coordinate, topics: List[Topic], i: Int): List[Attraction] = {
    dao.query(coordinate, i, topics)
  }

  override def receive: Actor.Receive = {
    case GetAllAttractions(request,range) =>
      println("GetAllAttractions --- ")
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
   /* def combine(attractionSource:Array[Attraction], totalElementNumber:Int, combinatoinNumber:Int,indexes:Array[Int], add:List[Attraction] => Unit): Unit = {
      val cha = totalElementNumber - combinatoinNumber
      println("cha:"+cha)
     for (ri <- combinatoinNumber to totalElementNumber) {//.foreach {// case i => {
              val i =  totalElementNumber - (cha - (totalElementNumber-ri))
              indexes(combinatoinNumber - 1) = i - 1;
              if (combinatoinNumber > 1) {
                println(combinatoinNumber+"<<<<<<<")
                combine(attractionSource, i - 1, combinatoinNumber - 1, indexes, add)
              }
              else {
                add((for (j <- 0 to combineNumber - 1) yield {
                  println(">>>>>>>>"+indexes(combineNumber - 1-j))
                  attractions(indexes(combineNumber - 1-j))
                }).toList)
              }
      //      }
      }
    }*/



    var result: List[List[Attraction]] = List()
   // combine(attractions.toArray,attractions.length,combineNumber,Array[Int](attractions.length),(list => result = list :: result ))
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


