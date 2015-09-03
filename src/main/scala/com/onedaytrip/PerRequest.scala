package com.onedaytrip

import akka.actor.{Actor, ActorRef, ReceiveTimeout}
import akka.event.Logging
import com.onedaytrip.domain._
import com.onedaytrip.service.TripService.{GetTrips}
import com.onedaytrip.service.UserService.{Search, Notification, Auth}
import org.json4s.DefaultFormats
import spray.http.StatusCode
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import spray.routing.RequestContext

import scala.concurrent.duration._

/**
 * Created by tomek on 6/11/15.
 */

trait ServiceRequest

//TODO: add list, or counter for responses in case of more then one actor will response
trait PerRequest extends Actor with Json4sSupport {
  import context._

  val ctx: RequestContext
  val targets: List[ActorRef]
  val message:OdtRequest

  val log = Logging(context.system, "PerReq")
  setReceiveTimeout(5.seconds)

  def composeMessage:ServiceRequest = ???

  targets.foreach(t => t ! composeMessage)

  def receive: Receive = {
    case response: OdtResponse => complete(OK, response)
    case ReceiveTimeout => complete(GatewayTimeout, "Timeout")
    case _ => complete(InternalServerError, "Error here")
  }

  def complete[T <: AnyRef](status: StatusCode, obj: T) = {
    log.debug("complete " + obj)
    ctx.complete(status, obj)
    stop(self)
  }

  implicit def json4sFormats = DefaultFormats
}

// TODO: use it instaed of ActorRef as targets
case class TargetSvc(responsive:Boolean, actorRef: ActorRef)

// This construction provides specific type of request, which will be send to many targets
object PerRequest {
  case class AuthRequest(ctx: RequestContext, targets: List[ActorRef], message:OdtRequest) extends PerRequest {
    override def composeMessage = Auth(message)
  }
  case class NotifyRequest(ctx: RequestContext, targets: List[ActorRef], message:OdtRequest) extends PerRequest {
    override def composeMessage = Notification(message)
  }
  case class TripRequest(ctx: RequestContext, targets: List[ActorRef], message:OdtRequest) extends PerRequest {
    override def composeMessage = GetTrips(message)
  }

}
