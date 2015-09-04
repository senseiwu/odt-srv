package com.onedaytrip.service

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import com.onedaytrip.domain.{OdtRequest, OdtResponse}

/**
 * Created by tomek on 7/10/15.
 */

object RequestHandler {
  case class Done(odtResponse: OdtResponse)
  case class Fail(message:String)
}

trait RequestHandler {
  this: Actor =>
  val log = Logging(context.system, "RequestHandler")
  case class Job(requester:ActorRef, worker:Props, request:OdtRequest)
  var requestNum = 0
  def runNext(queue:Vector[Job]):Receive = {
    requestNum+=1
    if (queue.isEmpty) waiting
    else {
      log.debug("RequestHandler: req " + requestNum)
      val worker = context.actorOf(queue.head.worker, s"auth$requestNum")
      worker ! queue.head.request
      running(queue)
    }
  }

  def receive: Receive = waiting

  def waiting: Receive = ???

  def running(queue:Vector[Job]):Receive = {
    case RequestHandler.Done(resp) =>
      log.debug("RequestHandler: DONE")
      val job = queue.head
      job.requester ! resp
      context.stop(sender)
      context.become(runNext(queue.tail))
  }
}
