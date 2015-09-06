package com.onedaytrip.service

import com.mongodb.casbah.Imports._
import com.onedaytrip.TestConfiguration
import com.onedaytrip.db.{shenzhen, Mongo}
import com.onedaytrip.domain.{TopicItem, Topic}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by tomek on 9/6/15.
 */
class ConverterTest extends FunSuite with BeforeAndAfter with TestConfiguration {

  val mongo = Mongo(MongoClient(), mongoDb.get)
//  val col = mongo.collection(poiCollection.get)
//  val topicCol = mongo.collection(topicCollection.get)
//  col.createIndex(MongoDBObject("loc" -> "2d"))

  val sz = new shenzhen

  val obj1 = MongoDBObject("topic" -> "amenity", "subtopics" -> MongoDBList("a","b","c"))
  val obj2 = MongoDBObject("topic" -> "park", "subtopics" -> MongoDBList("aa","bb","cc"))
  val obj3 = MongoDBObject("topic" -> "history", "subtopics" -> MongoDBList("aaa","bbb","ccc"))
  val list = MongoDBList(obj1, obj2, obj3)
  //topicCol.insert(MongoDBObject("topics" -> list))

  before {

  }

  after {
    mongo.getdb.dropDatabase()
  }

  test("check config1") {
    val topicCol = mongo.collection(topicCollection.get)
    assert(topicCol.name == "test-topic")
  }

  test("check config2") {
    val col = mongo.collection(poiCollection.get)
    assert(col.name == "test-poi")
  }

  test("mongocollection") {
    val topicCol = mongo.collection(topicCollection.get)
    val obj1 = MongoDBObject("topic" -> "amenity", "subtopics" -> MongoDBList("a","b","c"))
    val obj2 = MongoDBObject("topic" -> "park", "subtopics" -> MongoDBList("aa","bb","cc"))
    val obj3 = MongoDBObject("topic" -> "history", "subtopics" -> MongoDBList("aaa","bbb","ccc"))
    val list = MongoDBList(obj1, obj2, obj3)
    topicCol.insert(obj1)
    topicCol.insert(obj2)
    topicCol.insert(obj3)



    val topics = topicCol.find()
    assert(3 == topics.size)
    val tt = ArrayBuffer[TopicItem]()
    for(topic <- topics) {
      println(topic)
      val tts = ArrayBuffer[String]()
      val name = topic.getAs[String]("topic")
      val subjects = topic.get("subtopics")
      println(subjects.getClass)
      for(subject <- subjects.asInstanceOf[BasicDBList]) {
        tts += subject.toString
      }
      tt += TopicItem(name.toString, tts.toList)
    }
    println(" ** " + tt)
  }

}
