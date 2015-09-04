package com.onedaytrip.cache

import com.google.common.cache.{CacheLoader => GCacheLoader, LoadingCache, CacheBuilder}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by tomek on 7/5/15.
 */

trait CacheLoader[K,V] extends GCacheLoader[K,Future[V]] with Cacheable[K,V]

class Cache1[K <: AnyRef,V](dao:Cacheable[K,V]) extends CacheLoader[K,V] {
  //val dao:Cacheable[K,V]
  val cache = CacheBuilder.newBuilder().build(this)

  def get(key: K): Future[V] = cache.get(key)

  def put(key: K, value: V): Future[Unit] = {
    cache.put(key, Future {
      value
    })
    dao.put(key, value)
  }

  def remove(key: K): Future[Unit] = {
    cache.invalidate(key)
    dao.remove(key)
  }

  def close(): Unit = dao.close()

  def load(k: K): Future[V] = {
    println(" >> CACHE LOAD <<")
    dao.get(k)
  }
}

class Cache[K <: AnyRef,V] extends GCacheLoader[K,Future[V]] {
  var daoI:Cacheable[K,V] = _
  val cache = CacheBuilder.newBuilder().build(this)

  def get(key: K)(dao:Cacheable[K,V]): Future[V] = {
    daoI=dao
    cache.get(key)
  }

  def put(key: K, value: V)(dao:Cacheable[K,V]): Future[Unit] = {
    cache.put(key, Future {
      value
    })
    dao.put(key, value)
  }

  def remove(key: K)(dao:Cacheable[K,V]): Future[Unit] = {
    cache.invalidate(key)
    dao.remove(key)
  }

  def close(dao:Cacheable[K,V]): Unit = dao.close()

  def load(k: K): Future[V] = {
    println(" >>>>>>>>>> CACHE LOAD <<<<<<<<<")
    daoI.get(k)
  }
}

