package net.methvin.scalacache

import play.api.cache.CacheApi
import play.api.test.{PlaySpecification, WithApplication}
import play.cache.{CacheApi => JavaCacheApi}

import scalacache.ScalaCache
import scalacache.caffeine.CaffeineCache

class ScalaCacheApplicationSpec extends PlaySpecification {
  sequential

  "ScalaCache" should {
    "provide correct ScalaCache instance from injector" in new WithApplication() {
      val sc = app.injector.instanceOf[ScalaCache]
      sc.cache must beAnInstanceOf[CaffeineCache]
    }

    "provide correct CacheApi instance from injector" in new WithApplication() {
      val cacheApi = app.injector.instanceOf[CacheApi]
      cacheApi must beAnInstanceOf[ScalaCacheApi]
      val sc = app.injector.instanceOf[ScalaCache]
      cacheApi.asInstanceOf[ScalaCacheApi].cache must_== sc
    }

    "work" in new WithApplication() {
      val cacheApi = app.injector.instanceOf[CacheApi]
      cacheApi.set("foo", "bar")
      cacheApi.get("foo") must beSome("bar")
      cacheApi.remove("foo")
      cacheApi.get("foo") must beNone
    }

    "work with Java cache" in new WithApplication() {
      val cacheApi = app.injector.instanceOf[CacheApi]
      val javaCacheApi = app.injector.instanceOf[JavaCacheApi]
      cacheApi.set("foo", "bar")
      javaCacheApi.get[String]("foo") must_== "bar"
    }
  }
}
