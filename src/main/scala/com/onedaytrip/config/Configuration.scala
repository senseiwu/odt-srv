package com.onedaytrip.config

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
 * Created by tomek on 6/7/15.
 */
trait Configuration {
  val cfg = ConfigFactory.load()

  lazy val version = Try(cfg.getString("service.version")).getOrElse("unknown")
  lazy val odthost = Try(cfg.getString("service.host")).getOrElse("")
  lazy val odtport = Try(cfg.getInt("service.port")).getOrElse(8080)

  lazy val mongoDb = Try(cfg.getString("mongo.dbname"))
  lazy val topicCollection = Try(cfg.getString("mongo.topic-collection"))
  lazy val poiCollection = Try(cfg.getString("mongo.poi-collection"))
}
