package net.methvin.scalacache

import javax.inject.{Inject, Singleton}

import play.api.cache.CacheApi
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import play.cache.{CacheApi => JavaCacheApi, DefaultCacheApi => DefaultJavaCacheApi}

import scala.concurrent.duration._
import scala.reflect.ClassTag
import scalacache.ScalaCache

@Singleton
class ScalaCacheApi @Inject()(scalaCache: ScalaCache) extends CacheApi {
  implicit val cache = scalaCache

  // It seems like ScalaCache requires the durations to be finite, so we'll convert here
  private def toFinite(duration: Duration): Option[FiniteDuration] = duration match {
    case d: FiniteDuration => Some(d)
    case _ => None
  }

  override def get[A](key: String)(implicit ev: ClassTag[A]) = {
    scalacache.sync.get(key)
  }
  override def set(key: String, value: Any, expiration: Duration) = {
    scalacache.put(key)(value, toFinite(expiration))
  }
  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit ev: ClassTag[A]) = {
    scalacache.sync.get[A](key).getOrElse {
      val value = orElse
      scalacache.put(key)(value, toFinite(expiration))
      value
    }
  }
  override def remove(key: String) = {
    scalacache.remove(key)
  }
}

class ScalaCacheModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[CacheApi].to[ScalaCacheApi],
    bind[JavaCacheApi].to[DefaultJavaCacheApi]
  )
}

// mix this in for compile time DI
trait ScalaCacheComponents {
  def scalaCache: ScalaCache
  lazy val defaultCacheApi: CacheApi = new ScalaCacheApi(scalaCache)
}
