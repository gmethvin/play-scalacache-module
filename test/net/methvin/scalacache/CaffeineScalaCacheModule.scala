package net.methvin.scalacache

import javax.inject.Singleton

import com.github.benmanes.caffeine.cache.Caffeine
import play.api.inject.Module
import play.api.{Configuration, Environment}

import scalacache.ScalaCache
import scalacache.caffeine.CaffeineCache

class CaffeineScalaCacheModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration) = {
    // TODO: read cache settings from configuration?
    val caffeineCache = CaffeineCache(Caffeine.newBuilder().maximumSize(10000L).build[String, Object])
    Seq(
      bind[ScalaCache].to(ScalaCache(caffeineCache)).in[Singleton]
    )
  }
}
