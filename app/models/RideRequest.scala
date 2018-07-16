package com.beegrove.ride

import play.api.mvc._
import play.api.mvc.Result._
import play.api.Logger

case class RideRequest (request: Request[AnyContent]) {
  def inspect () = {
    Logger.debug("201807140858: inspect request" )
    Logger.debug("201807140858: request=" + this.request)
    Logger.debug("201807140858: request.uri=" + this.request.uri)
    Logger.debug("201807140858: request.method=" + this.request.method)
    Logger.debug("201807140858: request.path=" + this.request.path)
    Logger.debug("201807140858: request.headers=" + this.request.headers)
    Logger.debug("201807140858: request.queryString=" + this.request.queryString)
    Logger.debug("201807140858: request.body=" + this.request.body)
    Logger.debug("201807140858: request.cookies =" + this.request.cookies )
    Logger.debug("201807140858: request.flash =" + this.request.flash )
    Logger.debug("201807140858: request.session.data =" + this.request.session.data )
  }
  def getCookie(name:String): String = {
    val cookieOpt = this.request.cookies.get(name)
    if (cookieOpt.isEmpty) ""
    else cookieOpt.get.value
  }

  def parseGetQuery(names: Array[String]) = {
        // fields are the name part of a GET query
    var fieldMap = Map[String, String]()
    for (name <- names) {
      val vOpt = request.getQueryString(name)
      vOpt match {
        case Some(value) => fieldMap += (name -> value)
        case None    => fieldMap += (name -> "")
      }
    }
    fieldMap
  }
 

  def isAuthed = {
    if (this.request.session.get("profile.id").getOrElse("") == "")  false
    else true
  }

  def is3PartyAuthed  = {
    val id= this.getCookie("profile.id")  // linkedin id
    if( id == "" ) false
    else true
  }

  def isProfileComplete = {
    if (this.request.session.get("isProfileComplete").getOrElse("n") == "n")  false
    else true
  }

  def body = this.request.body
  def headers = this.request.headers
  def method = this.request.method
  def path = this.request.path
  def queryString = this.request.queryString
  def acceptLanguages = this.request.acceptLanguages
  def charset = this.request.charset
  def contentType = this.request.contentType
  def cookies = this.request.cookies
  def domain = this.request.domain
  def flash = this.request.flash
  def host = this.request.host
  def rawQueryString = this.request.rawQueryString
  def session = this.request.session
  override def toString = this.request.toString
}

