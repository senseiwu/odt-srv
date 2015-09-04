package com.onedaytrip.db.poi

import com.mongodb.casbah.Imports._
import com.onedaytrip.db.poi.common._

/**
 * Created by tomek on 8/29/15.
 */
object shop {
  val Key = "shop"

  val ValMall = "mall"
  val ValSupermarket = "supermarket"
  val ValBooks = "books"

  /**
   *      Key:name
    Key:operator
   */

  def mall(name:String) = shopType(shop.ValMall, MongoDBObject("name" -> name))
  def supermarket(name:String) = shopType(shop.ValSupermarket, MongoDBObject("name" -> name))
  def books(name:String) = shopType(shop.ValBooks, MongoDBObject("name" -> name))
}
