package com.onedaytrip.domain

/**
 * Created by tomek on 9/5/15.
 */
trait ServiceRequest
case class DataRequest(odtRequest: OdtRequest) extends ServiceRequest
case class TripRequest(odtRequest: OdtRequest) extends ServiceRequest
