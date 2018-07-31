package controllers

import javax.inject._
import play.api._
import play.api.mvc._
//import play.api.db.Databases
import play.api.db.Database
import play.api.db.NamedDatabase
import play.api.libs.json._




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

import io.getquill._

import com.beegrove.ride._
import models._
import models.db.tables._
import models.db.access.SQLResult

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
//class HomeController @Inject()(ec: MyExecutionContext, ws: WSClient, cache: AsyncCacheApi, cc: ControllerComponents) extends AbstractController(cc) 
class HomeController @Inject()(	ec: ExecutionContext
				, ws: WSClient
				, cache: AsyncCacheApi
				//, @NamedDatabase("ride") database:Database
				//, db:Database
				, cc: ControllerComponents) 
                             (
				implicit  @NamedDatabase("ride") database:Database
			     )
  extends AbstractController(cc) 
{
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

//  lazy val dbc = new PostgresAsyncContext[Literal](Literal, "db.ride");
   val dbc = new PostgresAsyncContext[Literal](Literal, "db.default");
//   val dbc = new PostgresAsyncContext[Literal]("db.default");

  val redirect_need_signin = Redirect("/").withNewSession.flashing( "flashMsgType" -> "error", "flashMsg" -> "Requested Action requires sign in. Please sign in")
  val redirect_signed_out = Redirect("/").withNewSession.flashing( "flashMsg" -> "You are signed out", "flashMsgType" -> "warn")
  val redirect_security_impossible = Redirect("/").withNewSession


  def todo = TODO

  def echo = Action {  implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    rideRequest.inspect ;
    Ok("Got request ") ;
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    Ok(views.html.index()) ;
  }

  def signout() = Action { implicit request: Request[AnyContent] =>
    redirect_signed_out
  }

  def search() = Action { implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    Ok("search") ;
  }


  def profile =  Action.async {  implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    val usrFrom3Party :UsrTable= UsrTable.userFrom3PartyAuth(rideRequest) ;

    val usrArray: JsArray = 
      SQLResult.run1("select row_to_json(a) from funcs.updateusr(in_oauth_id => ? , in_last_name => ? , in_first_name => ? , in_headline => ?) a"
                , Array(usrFrom3Party.oauth_id , usrFrom3Party.last_name, usrFrom3Party.first_name, usrFrom3Party.headline)
                , 1
      )
    val usrFromDb = UsrTable.fromJson((usrArray.value) (0)).get

    Future {
      checkSecurityAndThen(Ok(views.html.profile(usrFromDb))) ;
    }(ec)
  }

  def newtrip = Action {  implicit request: Request[AnyContent] =>
    val rideRequest = RideRequest (request) ;
    checkSecurityAndThen(Ok(views.html.trip())) ;
  }

  def savetrip = Action.async { implicit request: Request[AnyContent] =>  
    val rideRequest                             = RideRequest (request) ;
    val (authStatus, userFrom3ParthAuth, userFromSession)  = rideRequest.authStatus ;
    
    Future {
      authStatus match {
        case RideRequest.SIGNED_IN_COMPLETE_PROFILE => 
          val tripFromPost                = Trip.fromJson(rideRequest.parsePostQuery).get ;
          val trip_to_db   : Trip         = tripFromPost.copy(p1=tripFromPost.p1.copy(driver_id = userFromSession.usr_id))
          val trip_from_db : Option[Trip] = trip_to_db.upd
          Ok(views.html.trip(trip=trip_from_db.get, saved=true))
        case _                      => redirect_security_impossible
      } 
    }(ec)
  }

  def saveprofile = Action.async { implicit request: Request[AnyContent] =>  
    val rideRequest = RideRequest (request) ;
    val (authStatus, userFrom3ParthAuth, userFromSession)  = rideRequest.authStatus ;

    val js: JsValue = rideRequest.parsePostQuery ;
    val userFromPost = UsrTable.fromJson(js) ;
    Future {
      if (authStatus == RideRequest.SIGNED_IN_COMPLETE_PROFILE || authStatus ==  RideRequest.SIGNED_IN_INCOMPLETE_PROFILE)
      {
          // save to db
          val usrArray: JsArray = 
            SQLResult.run1("select row_to_json(a) from funcs.updateusr(in_oauth_id => ? , in_last_name => ? , in_first_name => ? , in_email => ? ) a"
                , Array(userFromSession.oauth_id , userFromPost.get.last_name, userFromPost.get.first_name, userFromPost.get.email)
                ,1 
            )
          val userFromDb = UsrTable.fromJson((usrArray.value) (0)).get
 	  Ok(views.html.profile(userFromDb, true))
      }
      else redirect_security_impossible
    }(ec)
  }

  def deposit = Action.async { implicit request: Request[AnyContent] =>  
    val rideRequest = RideRequest (request) ;
    val (authStatus, userFrom3ParthAuth, userFromSession)  = rideRequest.authStatus ;

    Future {
      if (authStatus == RideRequest.SIGNED_IN_COMPLETE_PROFILE )
      {
          val usrArray: JsArray = 
            SQLResult.run1("""select row_to_json(a) from usr a where a.usr_id = ?::uuid """
                , Array(userFromSession.usr_id)
                , 1
            )
          val userFromDb = UsrTable.fromJson((usrArray.value) (0)).get
 	  Ok(views.html.deposit(user=userFromDb))
      }
      else Ok(views.html.deposit(general_info_only=true ))
    }(ec)
  }

  def withdraw = Action.async { implicit request: Request[AnyContent] =>  
    val rideRequest = RideRequest (request) ;
    val (authStatus, userFrom3ParthAuth, userFromSession)  = rideRequest.authStatus ;

    Future {
      if (authStatus == RideRequest.SIGNED_IN_COMPLETE_PROFILE )
      {
          val usrArray: JsArray = 
            SQLResult.run1("""select row_to_json(a) from usr a where a.usr_id = ?::uuid """
                , Array(userFromSession.usr_id)
                , 1
            )
          val userFromDb = UsrTable.fromJson((usrArray.value) (0)).get
 	  Ok(views.html.withdraw(user=userFromDb))
      }
      else Ok(views.html.withdraw(general_info_only=true ))
    }(ec)
  }

  def savewithdraw = Action.async { implicit request: Request[AnyContent] =>  
    val rideRequest = RideRequest (request) ;
    val (authStatus, userFrom3ParthAuth, userFromSession)  = rideRequest.authStatus ;
    val js: JsValue = rideRequest.parsePostQuery ;
    val userFromPost = UsrTable.fromJson(js) ;

    val moneyTranxFromPost = Money_trnx.fromJson(js); 

    Future {
      if (authStatus == RideRequest.SIGNED_IN_COMPLETE_PROFILE )
      {
          val usrArray:  JsArray =
            SQLResult.run1("""select row_to_json(a)
                              from funcs.updateusr(in_oauth_id => ?, in_bank_email => ?) a """
                , Array(userFromSession.oauth_id, moneyTranxFromPost.get.bank_email)
                , 1
            )
          val userFromDb = UsrTable.fromJson((usrArray.value) (0)).get
 
          
          val trnxArray: JsArray = 
            SQLResult.run1("""select row_to_json(a) 
                              from funcs.update_money_trnx(usr_id => ?, trnx_cd => ?, bank_email => ?, requested_amount => ?) a 
                           """
                , Array(userFromSession.usr_id, Some(Money_trnx.values.WITHDRAW), moneyTranxFromPost.get.bank_email, moneyTranxFromPost.get.requested_amount)
                , 1
            )
          val trnxFromDb = Money_trnx.fromJson((trnxArray.value) (0)).get
 	  Ok(views.html.withdraw(user=userFromDb, money_trnx = trnxFromDb, saved=true))
      }
      else Ok(views.html.withdraw(general_info_only=true ))
    }(ec)
  }


  def linkedinCallback = Action.async { implicit reques: Request[AnyContent] =>  // use Asunc to do Future[WSResponse]
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
     	Ok(error_description)
      }(ec)
    }
  }

  def checkSecurityAndThen( result: Result)(implicit request :  Request[AnyContent]) : Result= {
    val rideRequest		= RideRequest(request);
    val (authStatus, userFrom3PartyAuth, userFromSession)  = rideRequest.authStatus ;

    authStatus match {
      case RideRequest.NOT_SIGNED_IN 		        => redirect_need_signin
      case RideRequest.CLIENT_SIDE_SIGNOUT		=> redirect_signed_out
      case RideRequest.SIGNED_IN_COMPLETE_PROFILE	=> result	
      case RideRequest.CASE_SHOULD_NEVER_HAPPEN		=> redirect_security_impossible
      case _                                            =>
      //RideRequest.CLIENT_SIDE_SIGNIN		
      //RideRequest.SIGNED_IN_INCOMPLETE_PROFILE
      //RideRequest.CLIENT_SIDE_SIGNIN_AS_DIFF_ID
        val userFromDb = UsrTable.userFromDb(userFrom3PartyAuth) ;
        if (userFromDb.isUserProfileComplete ) {  // User Profile at Database is complete
          result match {
           case null => result
           case _    =>
             result
              .withSession(request.session + (UsrTable.names.IS_PROFILE_COMPLETE -> UsrTable.values.PROFILE_COMPLETE) 
                                           + (UsrTable.names.OAUTH_ID -> userFromDb.oauth_id.get)
                                           + (UsrTable.names.USR_ID   -> userFromDb.usr_id.get)
                        )
              .flashing( "flashMsg" -> ("Welcome" + userFromDb.first_name) , "flashMsgType" -> "info")
          }
        }
        else // User Profile at Database is not complete
	{
          val result = Ok(views.html.profile(userFromDb))
          //Redirect(routes.HomeController.profile(user))
            .withSession(request.session + (UsrTable.names.OAUTH_ID -> userFromDb.oauth_id.get) 
                                         + (UsrTable.names.IS_PROFILE_COMPLETE -> UsrTable.values.PROFILE_IN_COMPLETE)
                                         + (UsrTable.names.USR_ID   -> userFromDb.usr_id.get)
                        )
            .flashing( "flashMsg" -> "Please complete your profile" , "flashMsgType" -> "warn")
          result
        }
    }
  }
}

