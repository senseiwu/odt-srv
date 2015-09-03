package com.onedaytrip.service

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.onedaytrip.service.UserService.Auth

/**
 * Created by tomek on 7/10/15.
 */
class StatisticService extends Actor {
  override def receive: Receive = {
    case Auth(msg) => println("STATICTIC: Auth received")
  }
}
