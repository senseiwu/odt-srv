package com.onedaytrip.service

import akka.actor.Actor
import com.onedaytrip.domain.TopicRequest

/**
 * Created by tomek on 7/10/15.
 */
class StatisticService extends Actor {
  override def receive: Receive = {
    case TopicRequest => println("STATICTIC: TopicRequest received")
  }
}
