package com.onedaytrip.map

import java.math.BigDecimal

import com.onedaytrip.domain.Coordinate


//import com.sun.tools.javac.util.List

/**
 * Created by tomek on 7/13/15.
 */
object route {
  def deg2rad(deg: Double) = deg * Math.PI / 180.0
  def rad2deg(rad: Double) = rad / Math.PI * 180.0
  def calculateDistance(c1:Coordinate, c2:Coordinate) = {
    val theta = c1.lon.toDouble - c2.lon.toDouble
    val dist = Math.sin(deg2rad(c1.lat.toDouble)) * Math.sin(deg2rad(c2.lat.toDouble)) + Math.cos(deg2rad(c1.lat.toDouble)) *
      Math.cos(deg2rad(c2.lat.toDouble)) * Math.cos(deg2rad(theta))
      Math.abs(
        Math.round(
          rad2deg(Math.acos(dist)) * 60 * 1.1515 * 1.609344 * 1000)
      )
  }

  val DistCoord = 0.009
  val DistMeters = 1000.0
  def range2dist(range:Int):Double = (range * DistCoord) / DistMeters

  def calculateShortestOrderOfCoordinates(from: Coordinate, source: List[Coordinate]): List[Coordinate] = {
    if(from == null) throw new Exception()

    def looper(from1: Coordinate, src:List[Coordinate], res:List[Coordinate]):List[Coordinate] = src match {
      case List() => res
      case head :: tail =>
        val fp = closestCoordination(from1, src)
        looper(fp, src.filter(_!=fp), res ::: List(fp))
    }

    looper(from, source, List())

//    var result = from :: Nil
//    var fromPoint = from
//    val sourceSize = source.length
//    for(i <- 1 to sourceSize) {
//      fromPoint = closestCoordination(fromPoint,source)
//      result = result ::: List(fromPoint)
//      src ＝ source.filter(_!=fromPoint)
//    }
//
//    result
  }

  def closestCoordination(from: Coordinate ,source :List[Coordinate] ): Coordinate= {
    val startPoint = from
    var closestOne :Coordinate = null
    var minDistance:BigDecimal = null
    var distance :BigDecimal= null
    for(nextOne <- source) {
      distance = getDistanceBetween(startPoint,nextOne)
      if(minDistance == null || distance.compareTo(minDistance) < 0) {
        minDistance = distance
        closestOne = nextOne
      }
    }
    closestOne
  }

  def  getDistanceBetween(x1: Coordinate ,x2: Coordinate ) :BigDecimal = {
    val x1x  = new BigDecimal(x1.lat)
    val x1y  = new BigDecimal(x1.lon)
    val x2x  = new BigDecimal(x2.lat)
    val x2y  = new BigDecimal(x2.lon)

    val a = x1x.subtract(x2x)
    val b = x1y.subtract(x2y)

    return new BigDecimal( Math.sqrt(a.multiply(a).add(b.multiply(b)).doubleValue()))

  }
}
