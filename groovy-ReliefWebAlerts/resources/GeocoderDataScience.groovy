/**
 * @author aozten
 *
 */
package com.palantir.dri

import groovyx.net.http.*
import groovy.json.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import org.apache.log4j.Level
import org.apache.log4j.Logger


def geocode(addressStr, String[] LatLon, log) {

	 // Test for Papua New Guinea (-6.314993,143.95555)
        if (addressStr  ==~ /(Papua New Guinea)/ ) {
		 LatLon[0] = -6.314993
		 LatLon[1] = 143.95555
		 LatLon[2] = ""
               log.log(Level.INFO, "latitude=" + LatLon[0]);
		 log.log(Level.INFO, "longitude=" + LatLon[1]);
		 log.log(Level.DEBUG	, "accuracy=" + LatLon[2]);

		 return 
	 }

 
	// Google (old)
	//def http = new HTTPBuilder( 'https://maps.googleapis.com'  )

	def http = new HTTPBuilder( 'http://www.datasciencetoolkit.org'  )
	log.log(Level.INFO, "Geocoding..." + addressStr);
	// perform a GET request, expecting JSON response data
	http.request( GET, TEXT ) {
 	//Data Science Tool Kit
	 uri.path =  '/maps/api/geocode/json'
	//Google
	 //uri.query =  [ address:"${addressStr}", key: 'AIzaSyBoMiG__g3I4YTzhTumsgim03WOT-PzHBU' ]
	 //Data Science Tool Kit
	 uri.query =  [ address:"${addressStr}" ]

	 headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
	 def jsonSlurper = new JsonSlurper()
	 // response handler for a success response code:
	 response.success = { resp, json ->
	 //println resp.status
	 // the following statement is for debugging only. Otherwise it consumes the stream
	 //System.out << reader
	  log.log(Level.INFO, "resp.status=" + resp.status);
	 def object = jsonSlurper.parse(json)
	
	 log.log(Level.INFO, "object.status=" + object.status);
	 def Object[] resultsarr = object.results
	 //assert resultsarr.size() == 1
	 log.log(Level.INFO, "Number of results=" + resultsarr.size());
	 if (resultsarr.size() > 0 ) {
		 LatLon[0] = resultsarr[0].geometry.location.lat
		 LatLon[1] = resultsarr[0].geometry.location.lng
		 LatLon[2] = resultsarr[0].geometry.location_type
		 }
		else {
		 LatLon[0] = null
		 LatLon[1] = null
		 LatLon[2] = null
		}
 		 log.log(Level.INFO, "latitude=" +  LatLon[0]);
		 log.log(Level.INFO, "longitude=" + LatLon[1]);
		 log.log(Level.DEBUG	, "accuracy=" + LatLon[2]);
	 } //response success
	
	 // handler for any failure status code:
	 response.failure = { resp ->
	   // println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
	   log.log(Level.ERROR, "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}");
	 }
	} // HTTP GET	
}
