package io.github.istreeter.jsonvalidator

import cats.effect.IO
import net.sf.ehcache.{Cache => UnderlyingCache, _}
import org.json4s.JValue
import scalacache._
import scalacache.ehcache._
import scalacache.serialization.binary._

object IOSchemaCache extends SchemaCache[IO] {

  implicit val mode: Mode[IO] = scalacache.CatsEffect.modes.async

  implicit lazy val ehcacheCache: Cache[JValue] = {
    val cacheManager = new CacheManager
    val underlying: UnderlyingCache = cacheManager.getCache("jsonvalidator")
    require(underlying != null, "missing cache jsonvalidator")
    EhcacheCache(underlying)
  }

  def putSchema(schemaId: String, schema: JValue): IO[Unit] =
    put(schemaId)(schema).map(_ => ())

  def getSchema(schemaId: String): IO[Option[JValue]] =
    get(schemaId)

}
