package com.onedaytrip.service

/**
 * Created by tomek on 6/24/15.
 */



import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.client.pipelining._
import spray.http.HttpHeaders.Accept
import spray.http.MediaTypes.`application/json`
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends App {
  implicit val system = ActorSystem()

  import system.dispatcher

  implicit val timeout = Timeout(5 seconds)
  // execution context for futures

  val pipeline: Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) <-
      IO(Http) ? Http.HostConnectorSetup("www.spray.io", port = 80)
    ) yield sendReceive(connector)

  val request = Get("/")
  val response: Future[HttpResponse] = pipeline.flatMap(_(request))

  response.onComplete{
    case Success(_) => println("OK")
    case Failure(e) => e.printStackTrace()
  }
}

case class UserP(userId:Int)
case class UserPost(userId:Int, id:Int, title:String, body:String)
case class Comment(postId:Int, id:Int, name:String, email:String, body:String)


object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat1(UserP)
  implicit val postFormat = jsonFormat4(UserPost)
  implicit val commentFormat = jsonFormat5(Comment)
}

object TypicodeUserPost extends App {
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  import MyJsonProtocol._

  val pipeline: HttpRequest => Future[Option[List[UserPost]]] = (
    encode(Gzip)
      ~> addHeader(Accept(`application/json`))
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[Option[List[UserPost]]]
    )
  val res:Future[Option[List[UserPost]]] = pipeline {
    //http://jsonplaceholder.typicode.com/posts?userId=1
    Get("http://jsonplaceholder.typicode.com/posts",UserP(1))
  }
  res.onComplete{
    case Success(post:Option[List[UserPost]]) => handleResponse(post.get)//println("POST: " + post.get.size)
    case Failure(e) => e.printStackTrace()
  }



  def handleResponse(posts:List[UserPost]) =
    posts foreach(p=>{
      println("UserId:" + p.userId)
      println("id: " + p.id)
      println("title: " + p.title)
      println("------------------------------------")
    })
}

object TypicodePostComments extends App {
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  import MyJsonProtocol._

  val pipeline: HttpRequest => Future[Option[List[Comment]]] = (
    encode(Gzip)
      ~> addHeader(Accept(`application/json`))
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[Option[List[Comment]]]
    )
  val res:Future[Option[List[Comment]]] = pipeline {
    //http://jsonplaceholder.typicode.com/posts/1/comments
    val url = buildUrlString("http://jsonplaceholder.typicode.com/posts/{}/comments",2)
    println("url: " + url)
    Get(url)
  }
  res.onComplete{
    case Success(post:Option[List[Comment]]) => handleResponse(post.get)//println("POST: " + post.get.size)
    case Failure(e) => e.printStackTrace()
  }


  def buildUrlString(url:String, v:Any*):String = {
    def buildUrl(url:String, v:List[Any]):String = v match {
      case Nil =>url
      case head :: tail =>buildUrl(url.replaceFirst("\\{}", head.toString), tail)
    }
    buildUrl(url, v.toList)
  }


  def handleResponse(posts:List[Comment]) =
    posts foreach(p=>{
      println("postId:" + p.postId)
      println("id: " + p.id)
      println("email: " + p.email)
      println("------------------------------------")
    })
}

object BuildM extends App {

  System.setProperty("java.net.preferIPv4Stack", "true")
  println("S: " + buildUrlString("user/{}/post/{}/line/{}", 1, "dd", 5))

  def buildUrl(url:String, v:Any*):String = {
    var vv:String = url
    v foreach (vl => {
      vv = vv.replaceFirst("(\\{})", vl.toString)
    })
    vv
  }

  def buildUrlString(url:String, v:Any*):String = {
    def buildUrl(url:String, v:List[Any]):String = v match {
      case Nil =>url
      case head :: tail =>buildUrl(url.replaceFirst("\\{}", head.toString), tail)
    }
    buildUrl(url, v.toList)
  }

}