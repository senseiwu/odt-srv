package com.onedaytrip.dao

import com.google.common.cache.CacheBuilder
import com.onedaytrip.db.Db.users

import scala.concurrent.Future

/**
 * Created by tomek on 6/7/15.
 */

class UserDao extends DbDao[String, Option[(Int,String,String)]] {
  import driver.api._
  //val cache = CacheBuilder.newBuilder().build(this)

  private def getPassword(n:String):DBIO[Option[String]] = users.filter(_.name === n).map(_.password).result.headOption
  private def getUserEx(n:String):DBIO[Option[(Int,String,String)]] = users.filter(_.name === n).result.headOption
  private def getUser(n:String):Future[Option[(Int,String,String)]] = dc.db.run(getUserEx(n))
  private def insert(n:String, p:String) = dc.db.run(DBIO.seq(users += (222, n, p)))

  override def get(key: String): Future[Option[(Int, String, String)]] = getUser(key)
  override def put(key: String, value: Option[(Int, String, String)]): Future[Unit] =
    insert(key, value.get._3)

  override def remove(key: String): Future[Unit] = ???
  override def close(): Unit = ???

}
