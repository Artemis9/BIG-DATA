/**
 * @author aozten
 * Written for Direct Relief International 
 * Palantir Data Feed from GDACS RSS
 */
package com.palantir.dri

//import com.palantir.geocode.*
//import com.palantir.geocode.api.*

import java.text.SimpleDateFormat
import java.util.Date
 
import org.apache.log4j.Level
import org.apache.log4j.Logger

def rss = new XmlSlurper().parse("http://www.gdacs.org/rss.aspx").declareNamespace(gdacs: 'http://www.gdacs.org', geo: 'http://www.w3.org/2003/01/geo/wgs84_pos#')

def String item_title, item_description, item_link, item_startdate, item_enddate
def String item_lat, item_long
def String item_eventtype,item_alertlevel
def String item_eventname
def String item_eventid, item_episodeid, item_severity_description, item_severity_unit, item_severity_value, item_population_description, item_population_unit,item_population_value
def String item_vulnerability, item_iso3, item_country, item_glide, item_version  
def List item_URLs =[]
 
def String startdate, enddate
def Date d
def Logger log = Logger.getLogger(GdacsRssReader.class);

item_array: rss.channel.item.each   { gdacs_item ->
 
  item_title = gdacs_item.title
  item_description = gdacs_item.description
  item_link = gdacs_item.link
 
  startdate= gdacs_item.'gdacs:fromdate'
  enddate= gdacs_item.'gdacs:todate'

  d = Date.parse('EEE, dd MMM yyyy HH:mm:ss zzz', startdate )
  item_startdate = d.format('yyyy-MM-dd HH:mm:ss') 
 
  d = Date.parse('EEE, dd MMM yyyy HH:mm:ss zzz', enddate )
  item_enddate = d.format('yyyy-MM-dd HH:mm:ss')
  
  item_lat= gdacs_item.'geo:Point'.'geo:lat'  
  item_long= gdacs_item.'geo:Point'.'geo:long' 
 
  item_eventtype= gdacs_item.'gdacs:eventtype'
  item_alertlevel= gdacs_item.'gdacs:alertlevel'
  item_eventname= gdacs_item.'gdacs:eventname'
  item_eventid= gdacs_item.'gdacs:eventid'
  item_episodeid= gdacs_item.'gdacs:episodeid'
 
  item_severity_description= gdacs_item.'gdacs:severity'
  item_severity_value= gdacs_item.'gdacs:severity'.@value
  item_severity_unit= gdacs_item.'gdacs:severity'.@unit
 
  item_population_description= gdacs_item.'gdacs:population'
  item_population_value= gdacs_item.'gdacs:population'.@value
  item_population_unit= gdacs_item.'gdacs:population'.@unit
 
  item_vulnerability= gdacs_item.'gdacs:vulnerability'.@value
  item_iso3= gdacs_item.'gdacs:iso3'
  item_country= gdacs_item.'gdacs:country'
  item_glide= gdacs_item.'gdacs:glide'
  item_version= gdacs_item.'gdacs:version'

  resource_array: gdacs_item.resources.'gdacs:resource'.each  { gdacs_resource ->
 	item_URLs.add(gdacs_resource.@url)
 }

 log.log(Level.INFO, "item_title: " + "${item_title}");
 log.log(Level.DEBUG, "item_description: " + "${item_description}");
 log.log(Level.DEBUG, "item_link: " + "${item_link}");
 log.log(Level.DEBUG, "item_startdate: " + "${item_startdate}");
 log.log(Level.DEBUG, "item_enddate: " + "${item_enddate}")
 log.log(Level.DEBUG, "item_lat: " + "${item_lat}");
 log.log(Level.DEBUG, "item_long: " + "${item_long}");
 log.log(Level.INFO, "item_eventtype: " + "${item_eventtype}");
 log.log(Level.INFO, "item_alertlevel: " + "${item_alertlevel}");
 log.log(Level.INFO, "item_eventname: " + "${item_eventname}");
 log.log(Level.INFO, "item_eventid: " + "${item_eventid}");
 log.log(Level.INFO, "item_episodeid: " + "${item_episodeid}");
 log.log(Level.INFO, "item_severity_description: " + "${item_severity_description}");
 log.log(Level.DEBUG, "item_severity_value: " + "${item_severity_value}");
 log.log(Level.DEBUG, "item_severity_unit: " + "${item_severity_unit}");
 log.log(Level.DEBUG, "item_population_description: " + "${item_population_description}");
 log.log(Level.DEBUG, "item_population_value: " + "${item_population_value}");
 log.log(Level.DEBUG, "item_population_unit: " + "${item_population_unit}");
 log.log(Level.DEBUG, "item_vulnerability: " + "${item_vulnerability}");
 log.log(Level.DEBUG, "item_iso3: " + "${item_iso3}");
 log.log(Level.INFO, "item_country: " + "${item_country}");
 log.log(Level.DEBUG, "item_glide: " + "${item_glide}");
 log.log(Level.DEBUG, "item_version: " + "${item_version}");
 log.log(Level.DEBUG, "item_URLs: " + "${item_URLs}");

def builder  = createBuilder()
	builder.with {		
			  def p = gdacs_alert ( id: "GDACS_EVENT_${item_eventid}_${item_episodeid}",
								externalId: "GDACS_EVENT_${item_eventid}_${item_episodeid}",
								startTime: item_startdate, endTime: item_enddate) {
								
				Address(latitude: item_lat, longitude: item_long) {
					country item_country
				}
				
				 event_title item_title
				 description item_description
				 url  item_link

 				 item_URLs.each() {
					url  it
				 }
			 
				 gdacs_event_type   item_eventtype
				 gdacs_alert_level   item_alertlevel
				 emergency_name   item_eventname
				 gdacs_event_id   item_eventid
				 gdacs_episode_id   item_episodeid
				 
				 gdacs_event_severity {
					 unit item_severity_unit
					 severity item_severity_value
					 description item_severity_description
				 } 
				 
				 gdacs_affected_population {
					 unit item_population_unit
					 population item_population_value
					 description item_population_description
				 }
				 
				 gdacs_vulnerability   item_vulnerability
				 
				 country_iso3   item_iso3
				 country_full_name item_country
				 country item_country
				 glide_number item_glide
				 gdacs_version item_version				 				 			
			} //p			
	} //builder 
	serialize builder, 'GDACS_EVENT_' + "${item_eventid}_${item_episodeid}" + '.xml' 
 }  //each
shutdown()





















