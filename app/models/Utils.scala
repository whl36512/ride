package models

import play.api.mvc._
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.ws._
import scala.concurrent.duration._
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import scala.concurrent.ExecutionContext.Implicits.global

object Utils {

  def sendWSRequest(wsc: WSClient, method: String, url: String, headers : Map[String, String] , queries: Map[String, Seq[String]]):Future[WSResponse] = {
        //var futureResponse: Future[WSResponse] = null;
        Logger.info("INFO 201807122118 headers=" + headers )
        Logger.info("INFO 201807122118 queries.size=" + queries.size )
        Logger.info("INFO 201807122118 queries=" + queries )

        var request: WSRequest = wsc.url(url)
                                .withRequestTimeout(2000.millis).withRequestFilter(AhcCurlRequestLogger())

        if (headers!= null) request= request.addHttpHeaders(headers.toSeq: _*)
        if (method=="GET") {
                if (queries!= null) {
                        request = request.addQueryStringParameters(queries.map { case (k, v) => (k, v(0)) }.toSeq: _*)
                }
                request.get()  // return Future[WSResponse]
        }
        else // method=="POST"
        {
                request.post(queries)
        }
  }

  def getLinkedInUserProfile (wsc: WSClient, access_token: String) : Future[String] = {
//GET /v1/people/~ HTTP/1.1
//Host: api.linkedin.com
//Connection: Keep-Alive
//Authorization: Bearer AQXdSP_W41_UPs5ioT_t8HESyODB4FqbkJ8LrV_5mff4gPODzOYR
//GET https://api.linkedin.com/v1/people/~?format=json

    val host ="https://api.linkedin.com/v1/people/~:(first-name,last-name,email-address,id,num-connections,picture-url,headline)"
    val method="GET"
    val headers = Map[String, String]("Connection" -> "Keep-Alive", "Authorization" -> ("Bearer " +access_token))
    val queries = Map[String, Seq[String]]("format" -> Seq("json"))
    val futureResponse = sendWSRequest(wsc, method, host, headers, queries) // return  Future[WSResponse]
    futureResponse.map {
      response =>
      Logger.info("201807130047" + response.body)
      response.body
    }
  }

  def linkedinPostAccessCode(wsc: WSClient, code: String) = {

//POST /oauth/v2/accessToken HTTP/1.1
//Host: www.linkedin.com
//Content-Type: application/x-www-form-urlencoded

//grant_type=authorization_code&code=987654321&redirect_uri=https%3A%2F%2Fwww.myapp.com%2Fauth%2Flinkedin&client_id=123456789&client_secret=shhdonottell
        val headers = Map[String, String]()
        var queries = Map[String, Seq[String]]()
        val host="https://www.linkedin.com"
        val action="/oauth/v2/accessToken"
        val method="POST"
        //val contentType="application/x-www-form-urlencoded"
        val client_secret="G3ihVrYkqIu0FWWd"
        val client_id="86xvjldqclucd9"
        //val redirect_uri="http://rideshare.beegrove.com:9000/linkedin/accesstoken"
        val redirect_uri="http://rideshare.beegrove.com:9000/linkedin/callback"

        queries =queries.+(     ("grant_type"   , Seq("authorization_code"))
                ,       ("code"         , Seq(code))
                ,       ("redirect_uri" , Seq(redirect_uri))
                ,       ("client_id"    , Seq(client_id))
                ,       ("client_secret", Seq(client_secret))
                )
        val futureResponse = sendWSRequest(wsc, method, host+action, null, queries) // return  Future[WSResponse]
        futureResponse.map {
          response =>
          Logger.info("INFO 201801171947: " + response.body)
          val access_token = (response.json \ "access_token" ).as[String]
          access_token
        }
    }
}

