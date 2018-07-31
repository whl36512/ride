package models.db.access

import play.api.mvc._
import play.api.db._
import java.sql.ResultSet
import org.json.JSONArray
import org.json.JSONObject
import scala.collection.mutable.ArrayBuffer
import play.api.Logger
import play.api.libs.json._
import models.db.tables._

//class SQLReslut @Inject() (db: Database, SQL: String) extends ResultSet {

object SQLResult  {

  def apply(SQL: String)(implicit db: Database): JsArray= {

    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs : ResultSet = stmt.executeQuery(SQL)
      ResultSetConverter.toJSONArray3(rs)

    } finally {
      conn.close()
    }
  }  
  
/*
  def apply (db: Database, SQL: String, params: Array[String]) : ArrayBuffer[Map[String, String]]  = {
    val conn = db.getConnection()
    try {
      val stmt = conn.prepareStatement(SQL)
      
      val paramCount= if (params==null)  0 else params.length
      for (i <- 0 until  paramCount)
      {
        stmt.setObject(i+1, params(i))
      }
      stmt.execute()
      val rs = stmt.getResultSet()

      return ResultSetConverter.toMapArray(rs)
    } finally {
      conn.close()
    }

  }
*/

  def apply (SQL: String, params: Array[String], expectedRow:Int = 1)(implicit db:Database) : JsArray  = {
  // the SQL should produce Json. each one json string per row
    Logger.debug(s"201807240936 SQLResult.apply SQL=$SQL")
    Logger.debug("201807240936 SQLResult.apply params=" + params.mkString(",") )
    val conn = db.getConnection()
    try {
      val stmt = conn.prepareStatement(SQL)
      
      val paramCount= if (params==null)  0 else params.length
      for (i <- 0 until  paramCount)
      {
        stmt.setObject(i+1, params(i))
      }
      stmt.execute()
      val resultSet = stmt.getResultSet() // expect rs in json format
      val jsArray =  ResultSetConverter.toJSONArray3(resultSet)

      conn.close()

      ( expectedRow, jsArray.value.size) match {

        case (1, 1) => jsArray
        case (1, 0) => jsArray
        case (1, _) => 
          Logger.error("201807261641 SQLResult.apply expect 1 row, but retrieved multiple rows. Cleaning output.")
          new JsArray()
        case _      => jsArray
      }
      
    } finally {
      conn.close()
    }
  }

  def run1 (SQL: String, params: Array[Option[String]], expectedRow:Int )(implicit db:Database) : JsArray  = {
    val paramCount= if (params==null)  0 else params.length
    val stringParams = params.map { case None => null; case Some(y) => y }
    apply ( SQL, stringParams, expectedRow)
  }
}


/**
 * Utility for converting ResultSets into some Output formats
 */
object ResultSetConverter  {
  /**
   * Convert a result set into a JSON Array
   * @param resultSet
   * @return a JSONArray
   * @throws Exception
   */

  def apply(resultSet: ResultSet)  = {
    //var jsonArray = new JSONArray();
    var jsonArray = ArrayBuffer.empty[JSONObject]
    if (resultSet.next()) {
      val total_cols = resultSet.getMetaData().getColumnCount();

      do {

        var obj = new JSONObject();
        for (i <- 1 to total_cols) {
          obj.put(resultSet.getMetaData().getColumnLabel(i)
            .toLowerCase(), resultSet.getObject(i));
        }
        jsonArray += obj
      } while (resultSet.next())
    }
    jsonArray
  }

  def toJSONArray(resultSet: ResultSet) = {
    var jsonArray = new JSONArray();
    if (resultSet.next()) {
      val total_cols = resultSet.getMetaData().getColumnCount();

      do {

        var obj = new JSONObject();
        for (i <- 1 to total_cols) {
          obj.put(resultSet.getMetaData().getColumnLabel(i)
            .toLowerCase(), resultSet.getObject(i));
        }
        jsonArray.put( obj)
      } while (resultSet.next())
    }
    jsonArray
  }

  def toJSONArray2(resultSet: ResultSet) = {
  //result set is in json string, one string per row
    var jsonArray = new JSONArray();

    if (resultSet.next()) {
      do {
        val row = resultSet.getObject(1).toString;
        val json:JSONObject = new JSONObject(row)
        jsonArray.put( json)
      } while (resultSet.next())
    }
    jsonArray
  }

  def toJSONArray3(resultSet: ResultSet) = {
  //result set is in json string, one string per row
    var jsonArray = new JsArray()
    val non_string_pattern = """:([0-9a-zA-Z\.\-]+),""".r
    val null_pattern       =""":"null",""".r                   ; //null should stay null instead of "null"

    if (resultSet.next()) {
      do {
        val row = resultSet.getObject(1).toString;
        val reformated_row = non_string_pattern.replaceAllIn(row, m => (s""":"${m.group(1)}",""" ) ) //double quote all unquoted value
        val reformated_row2 = null_pattern.replaceAllIn(reformated_row, m => ":null," ) //switch "null" back to null

        Logger.debug("201807240940 SQLResult.toJSONArray3 reformated_row2=" + reformated_row2)
        val json: JsValue = Json.parse(reformated_row2)
        //Logger.debug("201807240940 SQLResult.toJSONArray3 json=" + json)
        jsonArray = jsonArray :+  ( json)
        Logger.debug("201807240953 SQLResult.toJSONArray3 jsonArray="+jsonArray)
      } while (resultSet.next())
    }
    else {
      Logger.debug("201807240939 SQLResult.toJSONArray3 resultSet is empty")
    }
    Logger.debug("201807240939 SQLResult.toJSONArray3 jsonArray="+jsonArray)
    jsonArray
  }
  
  def toMapArray(resultSet: ResultSet) =
  {
    var table = ArrayBuffer.empty[Map[String, String]]
    if (resultSet == null) table
    else if (resultSet.next()) {
      val total_cols = resultSet.getMetaData().getColumnCount();
      
      do {

        var row =  Map[String, String]()
        for (i <- 1 to total_cols) {
          val colValue= resultSet.getObject(i)
          val colValString= if (colValue == null) "" else colValue.toString 
          row += (resultSet.getMetaData().getColumnLabel(i).toLowerCase() -> colValString)
        }
        table += row
      } while (resultSet.next())
    }
    table
  }

}
