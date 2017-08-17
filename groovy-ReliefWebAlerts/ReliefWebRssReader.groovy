/**
 * @author aozten
 * Written for Direct Relief International 
 * Palantir Data Feed from ReliefWeb RSS
 */

package com.palantir.dri

import groovyx.net.http.*
import groovy.json.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import com.google.common.base.Charsets
import com.google.common.io.Files
import java.text.SimpleDateFormat
import java.util.Date
 
import org.apache.log4j.Level
import org.apache.log4j.Logger

def geocode(addressStr, String[] LatLon, log) {

	// Google (requires a password)
	def http = new HTTPBuilder( 'https://maps.googleapis.com'  )

	//def http = new HTTPBuilder( 'http://www.datasciencetoolkit.org'  ) //has been down since March 25th.

	log.log(Level.INFO, "Geocoding..." + addressStr);
	// perform a GET request, expecting JSON response data
	http.request( GET, TEXT ) {
 	//Data Science Tool Kit or Google
	uri.path =  '/maps/api/geocode/json'
	// Google
	// uri.query =  [ address:"${addressStr}", key: 'AIzaSyBoMiG__g3I4YTzhTumsgim03WOT-PzHBU' ]
	uri.query =  [ address:"${addressStr}" ]
	// Data Science Tool Kit
	// uri.query =  [ address:"${addressStr}" ]

	headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
	def jsonSlurper = new JsonSlurper()
	// response handler for a success response code:
	response.success = { resp, json ->
	
	 	log.log(Level.DEBUG, "resp.status=" + resp.status);
	 	def object = jsonSlurper.parse(json)
	
	 	log.log(Level.DEBUG, "object.status=" + object.status);
	 	def Object[] resultsarr = object.results
	 
	 	log.log(Level.DEBUG, "Number of results=" + resultsarr.size());
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
	   	   log.log(Level.ERROR, "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}");
	 }
     } // HTTP GET	
}

def rss = new XmlSlurper().parse("http://reliefweb.int/disasters/rss.xml").declareNamespace(reliefweb: 'http://reliefweb.int:8080/reliefwebRssModule/').declareNamespace(dc: 'http://purl.org/dc/elements/1.1/')

def String item_title, item_description, item_link, item_pubdate, item_enddate
def String item_eventtype, item_key 
def String  item_iso3, item_country, item_glideNumber, item_status

def String item_lat, item_long
def String pubdate  
def Date d
def Logger log = Logger.getLogger(ReliefWebRssReader.class);

def List property_list
def List item_eventtypes
def List item_countries

// **** Definition used for geocoding
def String[] LatLon =new String[3]
def String tmpStr

item_array: rss.channel.item.each   { rw_item ->
def List item_iso3s = [] 
/**
 * Item title and Item description are read-in twice and second copy is appended to the first -don't know why, 
 * rss xml file shows data as one; including 'dc' nsamespace does not seem to make any difference
 */

 //item_title = rw_item.title
 //item_key = (item_title+"").replaceAll(/([ ?])/,'_').replaceAll( /([^a-zA-Z0-9_])/,'') 
 //item_description = rw_item.description
 
  item_link = rw_item.link
 
  pubdate= rw_item.pubDate
 
  d = Date.parse('EEE, dd MMM yyyy HH:mm:ss zzz', pubdate )
  item_pubdate = d.format('yyyy-MM-dd HH:mm:ss') 

/**
 * Another unreliable source: reliefweb namespace. (1) namespace URL changed over the night!!! 
 * (2) multiple tags sometimes comes as space separated; sometimes as appended; impossible to parse.
 */
  item_eventtype= rw_item.'reliefweb:disaster_type'
  item_iso3= rw_item.'reliefweb:iso3'
  log.log(Level.DEBUG, "\n******item_iso3: " + "${item_iso3}");
  if (item_iso3 ==~ /( )/ )
      item_iso3s = item_iso3.split(" ")
   else {
	 def int len = item_iso3.size()
        for (int i=0; i < len; i+=3 ){
		log.log(Level.DEBUG, "\n******item_iso3s: " + "${item_iso3s}");
 		log.log(Level.DEBUG, "\n******i: " + "${i}");
		log.log(Level.DEBUG, "\n******size(): " + "${item_iso3.size()}");
	if (i+3 < len) 
      		item_iso3s.add(item_iso3.substring(i, i+3))
	else   
 		item_iso3s.add(item_iso3.substring(i))
	}
  } //else

  item_country= rw_item.'reliefweb:country' 

  item_title = rw_item.'dc:title'
  item_description = rw_item.'dc:description' // has HTML characters in it
  
  log.log(Level.INFO, "\n******item_title: " + "${item_title}");

  log.log(Level.DEBUG, "dc item_description: " + "${item_description}");
  log.log(Level.DEBUG, "item_link: " + "${item_link}");
  log.log(Level.DEBUG, "item_pubdate: " + "${item_pubdate}");

  log.log(Level.DEBUG, "item_eventtype: " + "${item_eventtype}");
  log.log(Level.DEBUG, "item_iso3: " + "${item_iso3}");
  log.log(Level.DEBUG, "item_iso3s: " + "${item_iso3s}");
  log.log(Level.DEBUG, "item_country: " + "${item_country}");

// *** Start parsing description field ***
  property_list = item_description.replaceAll(/(\r|\n)/,'').split("<br/>")

  log.log(Level.DEBUG, "property_list: " + "${property_list}" );
  log.log(Level.DEBUG, "property_list lenght: " + "${property_list.size()}" );

if (property_list.size() >= 1) {
// *** Glide Number
  item_glideNumber =  property_list[0].replaceFirst(/(GLIDE number:)/,'').replaceAll(/( )/,'')
  log.log(Level.DEBUG, "item_glideNumber: " + "${item_glideNumber}" );
  item_key = item_glideNumber
// *** Event types
  item_eventtype = property_list[1].replaceFirst(/(Disaster type:)/,'').stripIndent()
  item_eventtypes = item_eventtype.split(", ") 
// *** Countries
   item_country = property_list[2].replaceFirst(/(Affected countries:)/,'').stripIndent()
   item_countries = item_country.split(", ")
// *** description text
  item_description =  property_list[3].replaceAll(/(<a|<\/a>|<p>|<\/p>|<br *\/>|<h3>|<\/h3>|<ul>|<\/ul>|<li>|<\/li>|>|<|\/>|<\/)/,'').stripIndent()
// *** item status
  item_status =  property_list[5].replaceFirst(/(Status:)/,'').stripIndent()
  log.log(Level.DEBUG, "item_status: " + "${item_status}" );
}
else {
// *** Countries
//item_countries = item_country.split(" ")
// *** Event types
// item_eventtypes = item_eventtype.split(" ")
// *** description text
  item_description =  item_description.replaceAll(/(<a|<\/a>|<p>|<\/p>|<br *\/>|<h3>|<h3\/>|<ul>|<\/ul>|<li>|<\/li>|>|<|\/>|<\/)/,'').stripIndent()
  item_key = (item_title+"").replaceAll(/([ ?])/,'_').replaceAll( /([^a-zA-Z0-9_])/,'')  
}

log.log(Level.DEBUG, "item_key: " + "${item_key}");
log.log(Level.DEBUG, "item_description: " + "${item_description}" );
log.log(Level.DEBUG, "item_eventtypes: " + "${item_eventtypes}" );
log.log(Level.DEBUG, "item_countries: " + "${item_countries}" );

// **** parse URLs
def List href_list = item_description.split("href=\"")
def List urls
log.log(Level.DEBUG, "href_list: " + "${href_list}" );
log.log(Level.DEBUG, "href_list.size(): " + "${href_list.size()}" );

// *** decide on enddate
item_enddate = (item_status == 'Past disaster') ? item_pubdate : ""

def builder  = createBuilder()
	builder.with {		
			  def p = reliefweb_alert ( id: "RELIEFWEB_EVENT_${item_key}",
								externalId: "RELIEFWEB_EVENT_${item_key}",
								startTime: item_pubdate, endTime: item_enddate ) {				
				 event_title item_title
				 glide_number  item_glideNumber
				 description item_description
				 status item_status
				 url  item_link
				 
				 for (int i=1;  i<href_list.size(); i++) {
				 	urls = href_list[i].split("\"")
				 	if (urls.size()>=1)  
  					 url urls[0] //forget the rest
				 }
				 item_iso3s.each() {
					country_iso3 it
				 }
				 item_countries.each() {
					tmpStr = it;
                                  geocode(tmpStr , LatLon ,log)
					Address(latitude: LatLon[0], longitude: LatLon[1]) {
				 		country tmpStr 
				 	}
					country tmpStr 
					country_full_name tmpStr 
				 }				
				 item_eventtypes.each() {
					reliefweb_disaster_type it
				 }		 			
			} //p			
	} //builder 
	serialize builder, 'RELIEFWEB_EVENT_' + "${item_key}" + '.xml' 
 }  //each
shutdown()











