/**
 * @author aozten
 *
 */
package com.palantir.dri

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.palantir.kea.csv.CSVRowTransformer
import groovyx.net.http.*
import groovy.json.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import org.apache.log4j.Level
import org.apache.log4j.Logger

def download(address)
{
	def file = new FileOutputStream(address.tokenize("/")[-1])
	def out = new BufferedOutputStream(file)
	out << new URL(address).openStream()	
	out.flush()
	out.close()	
}

def Logger log = Logger.getLogger(InteractionProjects.class);

def geocode(addressStr, String[] LatLon, log) {

 	 // Test for Papua New Guinea which should return (-6.314993,143.95555)
        if (addressStr  ==~ /(Papua New Guinea)/ ) {
		 LatLon[0] = -6.314993
		 LatLon[1] = 143.95555
		 LatLon[2] = "APPROXIMATE"
               log.log(Level.INFO, "latitude=" + LatLon[0]);
		 log.log(Level.INFO, "longitude=" + LatLon[1]);
		 log.log(Level.DEBUG	, "accuracy=" + LatLon[2]);

		 return 
	 }

	// Google
	//def http = new HTTPBuilder( 'https://maps.googleapis.com'  )
	 def http = new HTTPBuilder( 'http://www.datasciencetoolkit.org'  )

	log.log(Level.INFO, "Geocoding..." + addressStr);
	// perform a GET request, expecting JSON response data
	http.request( GET, TEXT ) {
 	//Data Science Tool Kit
	uri.path =  '/maps/api/geocode/json'
	//Google
	//uri.query =  [ address:"${addressStr}", key: 'AIzaSyBoMiG__g3I4YTzhTumsgim03WOT-PzHBU' ]
	// Data Science Tool Kit
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
	 }//response success
	
	 // handler for any failure status code:
	 response.failure = { resp ->
	   // println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
	   log.log(Level.ERROR, "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}");
	 }
	}	// HTTP GET	
}

download("http://www.ngoaidmap.org/sites/download/12.csv")

def reader = Files.newReader(new File('12.csv'), Charsets.ISO_8859_1)

def provider = new CSVRowTransformer()
def org_key
def org_name
def saved_name2  
def no_skip_flag = true
log.log(Level.DEBUG, "Before first provider");

provider.eachRow reader, { row ->
	
	org_name = (row.organization+"").replaceAll( /([^a-zA-Z0-9 ])/,'')
		
	no_skip_flag = !(org_name ==~ "Direct Relief") && !(saved_name2 == org_name)
	
	if (no_skip_flag) {	
		org_key = (row.organization+"").replaceAll(/([ ?])/,'_').replaceAll( /([^a-zA-Z0-9_])/,'') 
		def builder = createBuilder()
		builder.with {
			def p = NGO( id: "${org_key}" , externalId: "${org_key}") {
				organization_name org_name
				organization_id org_key
				saved_name2 = org_name
				log.log(Level.DEBUG, "org_name=${org_name}");
			} //p	 
		serialize builder, 'NGO_' + "${org_key}" + '.xml' 
			//Alternative syntax: "Person${row.id}.xml"
	} //builder
     }//if
} //provider loop

log.log(Level.DEBUG, "Before second provider");
def provider2 = new CSVRowTransformer()
def reader2 = Files.newReader(new File('12.csv'), Charsets.ISO_8859_1)

provider2.eachRow reader2, { row ->
	org_name = (row.organization+"").replaceAll( /([^a-zA-Z0-9 ])/,'')
 	if (!(org_name ==~ "Direct Relief")) {	
	   def builder2 = createBuilder()
	   builder2.with {
		 def p
		 org_key = (row.organization+"").replaceAll(/([ ?])/,'_').replaceAll( /([^a-zA-Z0-9_])/,'') 
		 
		 def project_name_normalized = (row.project_name+"").replaceAll( /([^a-zA-Z0-9 ])/,'')
		 		
		 def additional_information_normalized = (row.additional_information+"").replaceAll(/([^a-zA-Z0-9 ,;_!\\?\\.\\\\(\\)\\/\\:\\-\\'\\t\\n\\r\\+\\|©])/ ,'')
		 log.log(Level.INFO, "org_key = " + org_key + " row.interaction_intervention_id = " + row.interaction_intervention_id);
			 
		 p = humanitarian_aid_activity( id: "NGO_InterAction_${row.interaction_intervention_id}", 
					externalId: "NGO_InterAction_${row.interaction_intervention_id}", startTime: row.start_date, endTime: row.end_date) 
		{
			ngo_aid_map_project_id row.interaction_intervention_id
			id row.interaction_intervention_id
			project_name project_name_normalized
			event_title project_name_normalized
			organization_name org_name
			organization_id org_key
			// composite project_description (description, activities, additional_information)  
			project_description {				 
				short_description  row.project_description
				activities row.activities
				additional_information  additional_information_normalized
			}
			url row.project_website
			ngo_aid_map_estimated_people_reached row.estimated_people_reached
			ngo_aid_map_project_budget row.budget_numeric
				
			def tokenList = row.clusters.split(',')
			tokenList.each() {un_ocha_clusters it}
				  
			ngo_aid_map_project_contact_email row.project_contact_email
			ngo_aid_map_project_contact_person row.project_contact_person
			//ngo_aid_map_project_development_sector row.sectors
				  
			tokenList = row.sectors.split(',')
			tokenList.each() { ngo_aid_map_project_development_sector it}
				  			   
			tokenList = row.donors.split(',')
			tokenList.each() { ngo_aid_map_project_donors it}
				  			   
			tokenList = row.international_partners.split(',')
			tokenList.each() { ngo_aid_map_project_international_partners it}
				  
			tokenList = row.local_partners.split(',')
			tokenList.each() { ngo_aid_map_project_local_partners it}
				  
			ngo_aid_map_project_prime_awardee row.prime_awardee
			//ngo_aid_map_project_project_status
			status row.status
			 
			tokenList = row.location.split(',')
			def addressList
			def addressStr	
			def String[] LatLon =new String[3]
			tokenList.each() 
			{ 		
					addressList = it.split('>')  
					if (addressList.size() == 1 ) {							
						Country   addressList[0]
						addressStr = addressList[0]
						geocode(addressStr, LatLon,log)
						location_accuracy LatLon[2]
						
						Address(latitude: LatLon[0], longitude: LatLon[1]) { //latitude :  row.Latitude, longitude : row.Longitude
							Country                    addressList[0]
							}
					   	}
						else if (addressList.size() == 2 ) {
							Country   addressList[0]
							Region    addressList[1]
							addressStr =  addressList[1] + ", "  + addressList[0]
							geocode(addressStr, LatLon,log)
							location_accuracy  LatLon[2]
							Address(latitude: LatLon[0], longitude: LatLon[1]) { //latitude :  row.Latitude, longitude : row.Longitude
							State                      addressList[1]					 
							Country                    addressList[0]
							}
						}
						else if (addressList.size() == 3) {
							Country   addressList[0]
							Region    addressList[1]
							City   addressList[2]
							addressStr = addressList[2] + ", " + addressList[1] + ", " + addressList[0]
							geocode(addressStr, LatLon,log)
							location_accuracy  LatLon[2]

							Address(latitude: LatLon[0], longitude: LatLon[1]) { //latitude :  row.Latitude, longitude : row.Longitude
								City                       addressList[2]
								State                      addressList[1]
								Country                    addressList[0]
							}
						}
						else if (addressList.size() > 3)	{  								  
							Country   addressList[0]
							Region    addressList[1]
							City   addressList[2]
							addressStr = addressList[3]  + ", " + addressList[2] + ", " + addressList[1] + ", " +  addressList[0]  	
							geocode(addressStr, LatLon,log)
							location_accuracy  LatLon[2]

							Address(latitude: LatLon[0], longitude: LatLon[1]) { //latitude :  row.Latitude, longitude : row.Longitude
								//Address_1                  row.Street
								//Address_2                  row.Street2
								//Address_3                  row.Street3
								Address_4                  addressList[3]
								City                       addressList[2]
								State                      addressList[1]
								Country                    addressList[0]
							} //address
						} //else	
					 
				  } //multiple addresses/locations			  
			} //p
			def toOrg = _abstract(externalId: "${org_key}" )  
			links {
				  appears_in(parent:toOrg, child:p)
			 }			
		serialize builder2, 'Humanitarian_Aid_Activity_' + "${row.interaction_intervention_id}" + '.xml' 
		} //builder2
	 } // if org_name <> "Direct Relief"
} //provider

shutdown()