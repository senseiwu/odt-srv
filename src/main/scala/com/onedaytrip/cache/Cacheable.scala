package com.onedaytrip.cache

import scala.concurrent.Future


/**
 * Created by tomek on 6/27/15.
 */

trait Cacheable[K,V] {
  def get(key:K):Future[V]
  def put(key:K, value:V):Future[Unit]
  def remove(key:K):Future[Unit]
  def close():Unit
}


