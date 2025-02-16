package com.example.justdoit.service

import com.example.justdoit.common.util.JwtHelper
import com.example.justdoit.common.util.JwtHelper.UserClaim
import com.example.justdoit.config.AppConfig
import com.example.justdoit.service.JWTIssuer.User
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtClaim
import zio.*
import zio.json.*
import zio.json.DeriveJsonCodec
import zio.json.JsonCodec

import java.time.Instant

import JWTVerifier.*

trait JWTIssuer {
  def issueToken(user: User): UIO[String]
}

object JWTIssuer {
  def issueToken(user: User): URIO[JWTIssuer, String] = ZIO.serviceWithZIO[JWTIssuer](_.issueToken(user))

  case class User(username: String)

  val live = ZLayer.fromFunction((config: AppConfig) =>
    new JWTIssuer {
      private val secretKey = config.secret
      override def issueToken(user: User): UIO[String] =
        val claim = UserClaim(user.username)
        ZIO.succeed(JwtHelper.issueToken(claim, secretKey))
    }
  )
}
