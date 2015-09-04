package com.onedaytrip.service

import spray.json.DefaultJsonProtocol

/**
 * Created by tomek on 7/12/15.
 *
GET	/posts
GET	/posts/1
GET	/posts/1/comments
GET	/comments?postId=1
GET	/posts?userId=1
POST	/posts
PUT	/posts/1
PATCH	/posts/1
DELETE	/posts/1

 */

object PocRestClint {
  case class User(userId:Int)
  case class UserPost(userId:Int, id:Int, title:String, body:String)
  case class Comment(postId:Int, id:Int, name:String, email:String, body:String)
}

object PocJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat1(UserP)
  implicit val postFormat = jsonFormat4(UserPost)
  implicit val commentFormat = jsonFormat5(Comment)
}



class PocRestClient {

}
