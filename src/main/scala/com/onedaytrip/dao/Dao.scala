package com.onedaytrip.dao

import com.onedaytrip.cache.Cacheable
import com.onedaytrip.db.Db._

/**
 * Created by tomek on 6/20/15.
 */

trait Dao[K,V] extends Cacheable[K,V]

trait DbDao[K,V] extends Dao[K,V] {
  val dc = getDB
  val driver = dc.driver
}
