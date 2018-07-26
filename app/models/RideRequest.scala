package com.beegrove.ride

import play.api.mvc._
import play.api.mvc.Result._
import play.api.Logger
import play.api.libs.json._


case class RideRequest (request: Request[AnyContent]) {
  def inspect () = {
    Logger.debug("201807140858: inspect request begin -------------------------" )
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
    Logger.debug("201807140858: inspect request end -------------------------" )
  }

  def getCookie(name:String): String = {
    val cookieOpt = this.request.cookies.get(name)
    if (cookieOpt.isEmpty) ""
    else cookieOpt.get.value
  }
  def getSessionCookie(name:String): String = {
    this.request.session.data.get(name).getOrElse("")
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
 
  def parsePostQuery = {
// post query into json
    val data : Map[String, String] = request.body.asFormUrlEncoded.get.map { case (k, v) => ( k -> v(0) ) } // get form data from POST. Assumming each param has only one value
    Logger.debug (s"201807251152 RideRequest.parsePostQuery data=$data")
    val jsonString: String = "{" + data.map{case (k,v) => s""" "$k":"$v" """}.mkString(",") + "}"
    Logger.debug (s"201807251152 RideRequest.parsePostQuery jsonString=$jsonString")
    val jsValue: JsValue= Json.parse (jsonString)
    Logger.debug (s"201807251152 RideRequest.parsePostQuery jsValue=$jsValue")
    jsValue
  }

  def authStatus  = {
    val idInSession =this.getSessionCookie("profile.id") // linkedin id
    val idInCookie = this.getCookie("profile.id")  // linkedin id
    val isProfileComplete = this.getSessionCookie("isProfileComplete")  // linkedin id
    Logger.debug(s"201807161121     isProfileComplete=$isProfileComplete  idInSession=$idInSession     idInCookie=$idInCookie")
    
    val authStatus = (isProfileComplete, idInSession, idInCookie) match {
	case (z, "", "")  					=> RideRequest.NOT_SIGNED_IN
        case (z, "", x)   					=> RideRequest.CLIENT_SIDE_SIGNIN
        case (z, x , "")  					=> RideRequest.CLIENT_SIDE_SIGNOUT
        case (RideRequest.ProfileComplete, x , y) if x==y  	=> RideRequest.SIGNED_IN_COMPLETE_PROFILE
        case (z, x , y)                           if x==y  	=> RideRequest.SIGNED_IN_INCOMPLETE_PROFILE
        case (z, x , y) 			  if x!=y 	=> RideRequest.CLIENT_SIDE_SIGNIN_AS_DIFF_ID
	case _   						=> RideRequest.CASE_SHOULD_NEVER_HAPPEN
    }
    Logger.debug(s"201807161128 authStatus=$authStatus")
    authStatus
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

object RideRequest {
	final val NOT_SIGNED_IN  ="NOT_SIGNED_IN"
	final val CLIENT_SIDE_SIGNIN  ="CLIENT_SIDE_SIGNIN"
	final val CLIENT_SIDE_SIGNOUT ="CLIENT_SIDE_SIGNOUT"
	final val SIGNED_IN_COMPLETE_PROFILE  = "SIGNED_IN_COMPLETE_PROFILE"
	final val CLIENT_SIDE_SIGNIN_AS_DIFF_ID  ="CLIENT_SIDE_SIGNIN_AS_DIFF_ID"
	final val CASE_SHOULD_NEVER_HAPPEN  ="CASE_SHOULD_NEVER_HAPPEN"
	final val SIGNED_IN_INCOMPLETE_PROFILE="SIGNED_IN_INCOMPLETE_PROFILE"


        final val ProfileComplete ="ProfileComplete"
        final val ProfileInComplete ="ProfileInComplete"
}


