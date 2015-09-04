package com.onedaytrip.service

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import com.onedaytrip._
import com.onedaytrip.cache.Cache
import com.onedaytrip.dao.UserDao
import com.onedaytrip.domain._
import com.onedaytrip.service.UserService.{Search, Notification, Auth}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by tomek on 6/22/15.
 */

case class User(id:Int,name:String,password:String)

object UserCache extends Cache[String, Option[(Int,String,String)]]

class AuthSvc extends Actor {
  val log = Logging(context.system, "AuthSvc")
  val dao = new UserDao
  def receive: Receive = {
    case UserLogin(name, pass) =>
      log.debug("UserLogin req {}, {}", name, pass)
      checkUser(name,pass,sender)
    case UserSignIn(name, pass1, pass2) =>
      if(pass1.equals(pass2)) insertUser(name, pass1, sender)
      else sender ! RequestHandler.Done(UserStatus(name, UserStatus.PasswordNotMatch))
    case _ => sender ! RequestHandler.Done(UserStatus("", UserStatus.Failure))
  }

  def insertUser(name:String, passwd:String, target:ActorRef) = {
    UserCache.put(name, Option(222,name,passwd))(dao).onComplete {
      case Success(_) =>
        target ! RequestHandler.Done(UserStatus(name, UserStatus.Success))
      case Failure(e) => target ! RequestHandler.Done(UserStatus(name, UserStatus.Failure))
        println(e)
    }
  }

  def checkUser(n:String, p:String, target:ActorRef) = {
    UserCache.get(n)(dao).onComplete {
      case Success(Some((id, name, pass))) =>
        if(pass.equals(p)) target ! RequestHandler.Done(UserStatus(name, UserStatus.Success))
        else target ! RequestHandler.Done(UserStatus(n, UserStatus.Failure))
      case Success(None) => target ! RequestHandler.Done(UserStatus(n, UserStatus.UserNotFound))
      case Failure(e) => target ! RequestHandler.Done(UserStatus(n, UserStatus.Failure))
    }
  }
}

// TODO: add DAO calls without caching
class NotificationSvc extends Actor {
  val log = Logging(context.system, "NotificationSvc")
  override def receive: Actor.Receive = {
    case NotImplementedYet(text) => {
      log.debug("Chat to usedID: {} text: {}", "", text)
      sender ! Fine("Chat stored successfully")
    }
  }
}

class SearchSvc extends Actor {
  override def receive: Actor.Receive = ???
}

object UserService {
  case class Auth(request: OdtRequest) extends ServiceRequest
  case class Notification(request: OdtRequest) extends ServiceRequest
  case class Search(request: OdtRequest)
  //case class Done(response:OdtResponse)
  case class Failed()
}

class UserService extends Actor with RequestHandler {
  // TODO: I don't like this 'case'
  override def waiting: Receive = {
    case Auth(req) =>
      context.become(runNext(Vector(Job(sender, Props[AuthSvc], req))))
    case Notification(req) =>
      context.become(runNext(Vector(Job(sender, Props[NotificationSvc], req))))
    case Search(req) =>
      context.become(runNext(Vector(Job(sender, Props[SearchSvc], req))))
  }

}
