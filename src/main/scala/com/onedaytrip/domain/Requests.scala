package com.onedaytrip.domain

import java.util.Currency

import com.onedaytrip.db.poi.{leisure, shop}

/**
 * Created by tomek on 7/7/15.
 */

trait OdtRequest

case class TripRequestParams(startPointName: String, coordinates: String, activeness: String, topics: String, budget: String) extends OdtRequest
case class Activity(uid: Long, name: String = "") extends OdtRequest
case class Topic(uid: Long, name: String = "") extends OdtRequest
case class Budget(from: Float = 0, to: Float = 0, currency: Currency = Currency.getInstance("CNY"))

case class TopicRequest() extends OdtRequest
case class PoiRequest(coordinate: Coordinate, topics: List[String], range: Int)

object Activity {
  val L = "L"
  def apply(s:String) : Activity= {

    s match {
      case L =>
        Activity(1, "Low")
      case _ =>
        Activity(1000, "No Activity")

    }
  }
}

object Topic {
  val HISTORY = "HISTORY"

  def apply(s:String) : Topic= {
    s.toLowerCase match {
      case HISTORY =>
        println("Topic ---")
        Topic(1, HISTORY)
      case leisure.ValPark => Topic(2, leisure.ValPark)
      case shop.Key => Topic(3, "mall")
      case _ =>
        Topic(1000, "No Topic")
    }
  }
}


case class DTripRequest(startPoint: Coordinate, endpoint: Coordinate, activeness: Activity, topics: List[Topic], budget: Budget = null) extends OdtRequest

object DTripRequest {
  def apply(request: OdtRequest) :DTripRequest = {
    request match {
      case TripRequestParams(startPointName: String, coordinates: String, activeness: String, topics: String, budget: String) =>
        println("TripRequestParams ---")
        DTripRequest(Coordinate(coordinates),null, Activity(activeness),topics.split(",").map(Topic(_)).toList,null)
      case _ =>
        println("Exception!!! ---")
        throw new IllegalArgumentException("Can not identify the params")
    }
  }

}


