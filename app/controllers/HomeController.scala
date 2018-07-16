package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.cache._

// -- for Web Service call
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.libs.ws._
import play.api.http.HttpEntity

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext
// --

import play.api.Logger
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import scala.concurrent.ExecutionContext.Implicits.global

import com.beegrove.ride._
import models._

// Using a Future is only half of the picture though! If you are calling out to a blocking API such as JDBC, then you still will need to have your ExecutionStage run with a different executor, to move it off Play’s rendering thread pool
import play.api.libs.concurrent.CustomExecutionContext
trait MyExecutionContext extends ExecutionContext
class MyExecutionContextImpl @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "my.executor") with MyExecutionContext


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
//class HomeController @Inject()(ec: MyExecutionContext, ws: WSClient, cache: AsyncCacheApi, cc: ControllerComponents) extends AbstractController(cc) {
class HomeController @Inject()(ec: ExecutionContext, ws: WSClient, cache: AsyncCacheApi, cc: ControllerComponents) extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def todo = TODO

  def echo = Action {  implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    rideRequest.inspect ;
    Ok("Got request ") ;
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    rideRequest.inspect ;
    checkSecurityAndThen( Ok(views.html.index())) ;
  }


  def newoffer = Action {  implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    rideRequest.inspect ;
    
    Ok(views.html.newoffer()) ;
  }

  def saveoffer = Action.async { implicit request: Request[AnyContent] =>  // use Asunc to do Future[WSResponse]
    val rideRequest = RideRequest (request) ;
    rideRequest.inspect ;
    Future {
      Ok("saveoffer")
    }(ec)
  }


  def linkedinCallback = Action.async { reques =>  // use Asunc to do Future[WSResponse]
    //  /linkedin/callback?code=AQTDiXHOmjt-gINSGGqmhBScRsnJRkkd-nacS2x6Tc_uOP_CnhQFe384ZDc6Oh8Ob3mPUzud7MQvHgXYenc4ouGEHniS97pnNZUnFJXkW4FYsFDCwFSz7U4n8soSIw45Hpd6FhGYE0tRfj3V8NkKoEiLa9Nd4QWxyOPHQbJr&state=545aabc3-273d-4ea1-b23e-de78fddd05a1
    Logger.info("INFO 20170118221234: reques=" + reques)
    val rideRequest = RideRequest (reques) ;
    rideRequest.inspect ;

    val queryFields = rideRequest.parseGetQuery(Array("error", "error_description", "state", "code"))
    Logger.info("INFO 20170118221234: queryFields=" + queryFields)

// [Map(code -> AQRYFmhAKm7iXScAa5I1tMcPFi3wdZsUZFTAauxznycdOi__cN1iGGMw8GINwvzxqYBnjRybVLjWLYIucvAPMBvElRWmBw_ebl9WzUcgOplP-Pbwr-6u9yPbAFHVanATQdY_AE7vCfODtHCy_Qmv228etpMTKfw1tiY0d98Z
//		, state -> c276446d-2489-41da-a524-d883f5a1daa7)]
    val error	= queryFields("error")
    val error_description = queryFields("error_description")
    val state 	= queryFields("state")
    val code 	= queryFields("code")

    Logger.info("INFO 20170118221234: error=" + error)
    Logger.info("INFO 20170118221234: error_description=" + error_description)
    Logger.info("INFO 20170118221234: state=" + state)
    Logger.info("INFO 20170118221234: code=" + code)

    if ( error == ""  )
    {

	val futureLinkedinProfileJsonString : Future[String] = for {
	  access_token  <- Utils.linkedinPostAccessCode(ws, code)
          profileBody <- Utils.getLinkedInUserProfile(ws, access_token)
	} yield profileBody
	futureLinkedinProfileJsonString.map { profile => Ok(views.html.signincomplete(profile))}
    }
    else {
      Future {
     	Ok(views.html.linkedin_error(error_description))
      }(ec)
    }
  }

  def checkSecurityAndThen( result: Result)( implicit request :  Request[AnyContent]) : Result = {
    val rideRequest		= RideRequest(request);
    val isAuthed		= rideRequest.isAuthed ;
    val is3PartyAuthed		= rideRequest.is3PartyAuthed
    val isProfileComplete	= rideRequest.isProfileComplete 

    if ( isAuthed && is3PartyAuthed && isProfileComplete) result
    else if ( isAuthed && is3PartyAuthed && !isProfileComplete) {
      val user = User.userFrom3PartyAuth(rideRequest) ;
      if (user.isUserProfileComplete ) result.withSession(request.session + ("isProfileComplete" -> "y"))
      else Ok(views.html.profile(user)).flashing( "flashMsg" -> "Please complete your profile" )
    }
    else if ( isAuthed && ! is3PartyAuthed) { // signed out from linkedid at client side
      Ok(views.html.index()).withNewSession.flashing( "flashMsg" -> "You've been signed out")
    }
    else if ( ! isAuthed && is3PartyAuthed ) { // signed in into linkedin at client side
      val user = User.userFrom3PartyAuth(rideRequest) ;
      if (user.isUserProfileComplete ) {  // User Profile at Database is complete
        result	
		.withNewSession
		.withSession(request.session + "profile.id" -> user.id)
		.withSession(request.session + ("isProfileComplete" -> "y"))
		.flashing( "flashMsg" -> ("Welcome" + user.firstName) )
      }
      else // User Profile at Database is not complete
	Ok(views.html.profile(user))
		.withNewSession
		.withSession(request.session + "profile.id" -> user.id)
		.flashing( "flashMsg" -> "Please complete your profile" )
    }
    else // nots signed in
      result.withNewSession
  }
}

