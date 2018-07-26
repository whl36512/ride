package models.db.tables
import scala.concurrent.ExecutionContext.Implicits.global
//import dbc.DbContext
import play.api._
import play.api.db.Database
import com.beegrove.ride._

import java.time._
import java.util.UUID

import models.db.access._
import io.getquill.context.sql.SqlContext
import play.api.libs.json._


case class UsrTable(   
    usr_id              : Option[String] = None
    , first_name        : Option[String] = None
    , last_name         : Option[String] = None
    , email             : Option[String] = None
    , bank_email        : Option[String] = None
    , member_since      : Option[String] = None
    , trips_posted      : Option[Int]    = None
    , trips_completed   : Option[Int]    = None
    , rating            : Option[Double] = None
    , balance           : Option[Double] = None
    , oauth_id          : Option[String] = None
    , oauth_host        : Option[String] = None
    , c_ts              : Option[String] = None
    , m_ts              : Option[String] = None
) {
  def isUserProfileComplete : Boolean = {
    val isUserProfileComplete = !(email == "")
    Logger.debug (s"201807161447  isUserProfileComplete=$isUserProfileComplete")
    isUserProfileComplete;
  }
};

object UsrTable {
  def get(oauth_id:Option[String]) (implicit db: Database ):Option[UsrTable]  = {

    oauth_id match { 
      case None => return None
      case Some(oauth_id) => 
        val jsonArray:JsArray = SQLResult("select row_to_json(usr) from usr where usr.oauth_id=?", Array(oauth_id))
        jsonArray.value.size match {
          case 0 => Logger.debug("201807240933 UsrTable.get No rows found where usr.oauth_id="+ oauth_id)
                    return None
          case 1 =>  return fromJson((jsonArray.value)(0))
          case _ =>  Logger.error("201807232209 UsrTable.get Should never return more than one row jsonArray=\n" + jsonArray)
                 return None
        }
    }
  }
 
  type T = UsrTable
  def fromJson (jsValue: JsValue) : Option[T] = {
    implicit val jsonReads = Json.reads[T]
    val fromJson: JsResult[T] = Json.fromJson[T](jsValue)
    fromJson match {
      case JsSuccess(obj: T, path: JsPath) => 
        Logger.debug("201807252208 obj= " + obj)
        return Some(obj)
      case e: JsError => 
	Logger.error("201807250824 " + JsError.toJson(e).toString())
	return None
    }
  }

  def userFrom3PartyAuth (request: RideRequest): UsrTable = {
    val oauth_id= request.getCookie("profile.id")  // linkedin id
    val last_name= request.getCookie("profile.lastName")
    val first_name= request.getCookie("profile.firstName")
    val headline= request.getCookie("profile.headline")

    val user = UsrTable (oauth_id=Option(oauth_id), last_name=Option(last_name), first_name=Option(first_name));
    Logger.debug(s"201807161501 user=$user")
    user;
  }

  def userFromSession (request: RideRequest)  : UsrTable = {
    val id= request.session.get("profile.id").getOrElse("")  // linkedin id
    val lastName= request.session.get("profile.lastName").getOrElse("")
    val firstName= request.session.get("profile.firstName").getOrElse("")
    val headline= request.session.get("profile.headline").getOrElse("")

    val user = UsrTable (oauth_id=Option(id), last_name=Option(lastName), first_name=Option(firstName));
    user;
  }
}

// for Quil. Will not use
/*
case class Usr(   
    usr_id              : Option[java.util.UUID]
    , first_name        : Option[String]
    , last_name         : Option[String]
    , email             : Option[String]
    , bank_email        : Option[String]
    , member_since      : Option[java.time.LocalDateTime] 
    , trips_posted      : Option[Int]
    , trips_completed   : Option[Int]
    , rating            : Option[Float]
    , balance           : Option[Float]
    , oauth_id          : String
    , oauth_host        : Option[String]
    , c_ts              : Option[java.time.LocalDateTime]
    , m_ts              : Option[java.time.LocalDateTime]
) 
*/

/*
class Usrs(val dbctx: DbContext)
{
    import dbctx._

    val q = quote{ query[Usr] }
/*
 def update(db: Database) = {
  val r=SQLResult(db, "select * from usrupdate(oauth_id=?, email=?, oauth_host=?, last_name=?, first_name=?)", 
                                Array(this.oauth_id, this.email, this.oauth_host, this.last_name, this.first_name))
 }
 */
    def get(in_oauth_id: String) = {
	Logger.debug(s"201807221921 Usrs.get in_oauth_id=$in_oauth_id")
        dbctx.run(q.filter(u => u.oauth_id == lift(in_oauth_id))).map(_.headOption)
    }
}
*/

case class Trip(
  trip_id               : java.util.UUID                 
  , driver_id           : java.util.UUID                 
  , start_date          : java.time.LocalDate
  , end_date            : java.time.LocalDate
  , start_time          : java.time.LocalTime
  , start_loc           : String                   
  , start_display_name  : String                   
  , start_lat           : Double
  , start_lon           : Double
  , end_loc             : String                   
  , end_display_name    : String                   
  , end_lat             : Double
  , end_lon             : Double
  , distance            : Float                   
  , price               : Float                   
  , recur_ind           : Boolean                
  , status_code         : String
  , desc_txt            : String                   
  , seats               : Int                
  , day0_ind            : Boolean                
  , day1_ind            : Boolean                
  , day2_ind            : Boolean                
  , day3_ind            : Boolean                
  , day4_ind            : Boolean                
  , day5_ind            : Boolean                
  , day6_ind            : Boolean                
  , c_ts                : java.time.LocalDateTime
  , m_ts                : java.time.LocalDateTime
  , rec_creat_usr       : String     
)

case class Book (
    book_id             : java.util.UUID                   
    , trip_id           : java.util.UUID                     
    , rider_id          : java.util.UUID                     
    , trip_date         : java.time.LocalDate                     
    , price             : Float                     
    , money_to_driver   : Float                     
    , money_to_rider    : Float                     
    , book_status_cd    : String
    , rider_score       : Int                 
    , driver_score      : Int                 
    , rider_comment     : String                     
    , driver_comment    : String                     
    , book_ts           : java.time.LocalDateTime
    , driver_cancel_ts  : java.time.LocalDateTime
    , rider_cancel_ts   : java.time.LocalDateTime
    , finish_ts         : java.time.LocalDateTime
)

case class money_trnx (
    money_trnx_id   : java.util.UUID       
    , usr_id        : java.util.UUID         
    , trnx_type     : String
    , amount        : Float         
    , trnx_ts       : java.time.LocalDateTime
    , reference_no  : String         
    , cmnt          : String         
)
