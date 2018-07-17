package com.beegrove.ride
import play.api.Logger


import play.api.mvc._

case class User(id: String, lastName: String = "" , firstName: String = "" , headline: String = "", email:String ="" ) {
  def isUserProfileComplete : Boolean = {
    val isUserProfileComplete = true
    Logger.debug (s"201807161447  isUserProfileComplete=$isUserProfileComplete")

    isUserProfileComplete;
  }
  
}

object User{
  def userFrom3PartyAuth (request: RideRequest): User = {
    val id= request.getCookie("profile.id")  // linkedin id
    val lastName= request.getCookie("profile.lastName")
    val firstName= request.getCookie("profile.firstName")
    val headline= request.getCookie("profile.headline")

    val user = User (id, lastName, firstName, headline);
    Logger.debug(s"201807161501 user=$user")
    user;
  }

  def userFromSession (request: RideRequest)  : User = {
    val id= request.session.get("profile.id").getOrElse("")  // linkedin id
    val lastName= request.session.get("profile.lastName").getOrElse("")
    val firstName= request.session.get("profile.firstName").getOrElse("")
    val headline= request.session.get("profile.headline").getOrElse("")

    val user = User (id, lastName, firstName, headline);
    user;
  }

}
