/**
 * @author aozten
 *
 */
package com.palantir.dri

 
import groovyx.net.http.*
import groovy.json.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

/* https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyBoMiG__g3I4YTzhTumsgim03WOT-PzHBU */

def http = new HTTPBuilder( 'https://maps.googleapis.com'  )
 
// perform a GET request, expecting JSON response data
http.request( GET, TEXT ) {
  uri.path =  '/maps/api/geocode/json'
  //1600+Amphitheatre+Parkway,+Mountain+View,+CA
  uri.query =  [ address:'Warrap, South Sudan', key: 'AIzaSyBoMiG__g3I4YTzhTumsgim03WOT-PzHBU' ]

  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
  def jsonSlurper = new JsonSlurper()
  // response handler for a success response code:
  response.success = { resp, json ->
  println resp.status
  // the following statement is for debugging only. Otherwise it consumes the stream
  System.out << json 

  def object = jsonSlurper.parse(json)
  println object.status
  def Object[] resultsarr = object.results
  //assert resultsarr.size() == 1
  println "size=" + resultsarr.size()
  println resultsarr[0].geometry.location.lat
  println resultsarr[0].geometry.location.lng
 
  }

  // handler for any failure status code:
  response.failure = { resp ->
	println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
  }
}