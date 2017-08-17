package com.palantir.integration.disease

import java.text.SimpleDateFormat
import java.util.concurrent.*

import javax.xml.transform.stream.StreamSource

import net.sf.saxon.FeatureKeys
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.Serializer

import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.ccil.cowan.tagsoup.Parser as TagSoupParser

import com.palantir.integration.dsl.DslApplicationContext
import com.palantir.integration.dsl.json.Alert
import com.palantir.integration.dsl.json.AlertLocation
import com.palantir.integration.dsl.json.JsonRowTransformer
import com.palantir.integration.dsl.utils.IntegrationOutputResultFactory

/**
 * Script that fetches disease data from healthmap.org and then uses Kea
 * to generate PXMLs out of it. The script works by doing the following:
 * 1. Accesses a PHP web service from heathmap.org that returns records of
 *    disease data in JSON format. Some initial filtering of the data can be 
 *    done by command-line arguments. 
 * 2. Each record acquired from the web service is converted into a Java object
 *    representing a disease alert. This is done by a JSONRowTransformer.
 * 3. A thread pool is started for handling the parsing of alerts. A default of 120
 *    threads is launched, but this can be configured via a command-line argument.
 * 4. Each record contains a link pointing to a web page with more info on that
 *    alert. These links are accessed and their contents stored during parsing.
 *    Since all links are re-directed from heathmap.org, a request interval
 *    is used between accesses (default: 5ms) to avoid DoS'ing their servers.
 *    In essence, what happens is that a new thread is only allowed to run
 *    'requestInterval' milliseconds after the previous one.
 * 
 */

Logger log = Logger.getLogger(HealthAlertDocImporter.class);

// Command-line argument processing.
Options cliOptions = new Options()
cliOptions.addOption("k", "authKey", true, "The authorization key to use with HealthMap")
cliOptions.addOption("sd", "startDate", true, "The date to query from, yyyy-mm-dd, default 7 days previous")
cliOptions.addOption("ed", "endDate", true, "The date to query to, yyyy-mm-dd, default: current date")
cliOptions.addOption("cc", "countryCodes", true, "The HealthMap country codes, comma-separated, default: all")
cliOptions.addOption("dc", "diseaseCategories", true, "The HealthMap disease categories, comma-separated, default: all")
cliOptions.addOption("f", "feedIds", true, "The HealthMap feed IDs, comma-separated, default: all English-language feeds")
cliOptions.addOption("l", "language",true, "The HealthMap language code, default: English")
cliOptions.addOption("t", "ThreadCound", true, "The max number of simultaneous page-grabs by the crawler, default: 120")
cliOptions.addOption("i", "requestInterval", true, "The interval between requests in ms, default: 5")

CommandLine cliConfig = new BasicParser().parse(cliOptions, args)
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd")

String sDateStr = cliConfig.getOptionValue("sd", null)
Date sDate = null
if(sDateStr != null) sDate = dateFormatter.parse(sDateStr)

String eDateStr = cliConfig.getOptionValue("ed", null)
Date eDate = null
if(eDateStr != null) eDate = dateFormatter.parse(eDateStr)

String ccOpt = cliConfig.getOptionValue("cc", null)
String[] countryCodes = ccOpt == null ? null : ccOpt.split(",")

String dcOpt = cliConfig.getOptionValue("dc", null)
String[] diseaseCategories = dcOpt == null ? null : dcOpt.split(",")

String fOpt = cliConfig.getOptionValue("f", null)
String[] feeds = fOpt == null ? null : fOpt.split(",")

String language = cliConfig.getOptionValue("l", null)

String authKey = cliConfig.getOptionValue("k", "cnbhgf73kdhw057bkqoeruhgu44765")

URI uri = constructURI(authKey, sDate, eDate, countryCodes, diseaseCategories, feeds, language)
log.log(Level.INFO, "Query uri: " + uri)

String threadCountStr = cliConfig.getOptionValue("t", "120")
int threadCount = 120
try{
	threadCount = Integer.parseInt(threadCountStr)
} catch(NumberFormatException ex) {
	println(threadCount + " could not be parsed to a string. Default to 120 for ThreadCount")
	threadCount = 120
}

String requestIntervalStr = cliConfig.getOptionValue("i", "5")
int requestInterval = 5
try {
	requestInterval = Integer.parseInt(requestIntervalStr)
} catch(NumberFormatException e) {
	println(requestInterval + " could bot be parsed to an int. Defaulting to 5 ms for requestInterval")
	requestInterval = 5
}

// Replicating some work done by BaseDataIntegrationScript in order to get the path
// to the PXML directory. This is done only once per execution.
final String[] contextClasspaths = [ "classpath:baseKeaContext.xml", "classpath*:keacontext*.xml" ]
DslApplicationContext appContext = new DslApplicationContext(contextClasspaths)
IntegrationOutputResultFactory resultFactory = appContext.getResultFactory()

// The JSON record from the URI is converted into an array of Java objects.
Alert[] alerts = JsonRowTransformer.getAlerts(uri)

log.log(Level.INFO, "Found " + alerts.length + " alerts");
log.log(Level.INFO, "Using request interval of " + requestInterval + "ms");
int numThreads = (threadCount > alerts.length ? alerts.length : threadCount)
log.log(Level.INFO, "Using pool of " + numThreads + " threads");

// Start a thread pool, limiting the number of threads to the number of alerts to be processed.
ExecutorService executor = Executors.newFixedThreadPool(numThreads)
// Wraps the time of the last access to the server. We wrap it so it can also be used as a mutex.
final DateWrapper lastAccess = new DateWrapper()

// Fire a thread for each alert. ExecutorService guarantees at most 'threadCount' threads simultaneously.
alerts.each() {
	final Alert alert = (Alert)it;
	Runnable runnable = new Runnable() {
		public void run() {
			handleAlert(alert, appContext.getResultFactory(), lastAccess, (long) requestInterval, log);
		}
	}
	executor.execute(runnable)
}
executor.shutdown()
executor.awaitTermination(40L, TimeUnit.MINUTES)
shutdown()

/**
 * Processes an alert, visiting the link pointed by it and using Kea to serialize
 * all information to a PXML.
 * 
 * @param alert The alert to be processed.
 * @param resultFactory This is used to get a path to the PXML directory, to handle
 * resolution of duplicate records.
 * @param lastAccess Wraps the date object of the last time a thread was launched.
 * Used for thread synchronization.
 * @param requestInterval The interval, in ms, between threads. Used for synchronization.
 * @param log The Log4j object.
 */
private void handleAlert(Alert alert, IntegrationOutputResultFactory resultFactory, DateWrapper lastAccess, long requestInterval, Logger log) {
	// When we have an error while accessing the record's link, this is the string we set to it.
	String errorStr = "**UNKNOWN**"
	
	// Now we start processing all the fields from the alert.
	AlertLocation location = alert.getLocation();

	def link = alert.getLink()

	def htmlProcessor = new Processor(false)
	def htmlDocumentBuilder = htmlProcessor.newDocumentBuilder()
	htmlProcessor.setConfigurationProperty(FeatureKeys.STRIP_WHITESPACE, "all")
	htmlProcessor.setConfigurationProperty(FeatureKeys.SOURCE_PARSER_CLASS, "org.ccil.cowan.tagsoup.Parser")

	def tagSoupParser = htmlProcessor.underlyingConfiguration.sourceParser
	tagSoupParser.setFeature TagSoupParser.ignoreBogonsFeature, true
	htmlProcessor.underlyingConfiguration.reuseSourceParser tagSoupParser

	def html
	def urlConnection
	def bufferedUrlInputStream
	def urlInputStream
	def urlIsReader
	def linkUrl = new URL(link)
	def finalUrl
	def reader
	try{
		// Thread synchronization. Since this method is called by several threads, execution only passes this point
		// when the time interval since the last thread reaches 'requestInterval'.
		synchronized(lastAccess) {
			Date current = new Date()
			// If it's too soon, we wait.
			while(lastAccess.getDateObj() != null && (current.getTime() - lastAccess.getDateObj().getTime()) < requestInterval) {
				try {
					lastAccess.wait((long) (requestInterval / 2L))
					current = new Date()
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			// We are now going to run, update last access time and wake other threads.
			lastAccess.setDateObj(current);
			lastAccess.notifyAll();
		}
		// Fetch the link's contents and set it to 'errorStr' in case of errors.
		urlConnection = linkUrl.openConnection()
		urlConnection.setReadTimeout(5 * 60 * 1000); // 5 minute timeout
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
		urlInputStream = urlConnection.getInputStream()
		// All URLs point to healthmap.org, which then redirects elsewhere. Variable 'finalUrl' holds the redirected URL.
		finalUrl = urlConnection.getURL()
		bufferedUrlInputStream = new BufferedInputStream(urlInputStream)
		String contentEncoding = urlConnection.getContentEncoding()
		if(contentEncoding == null) {
			contentEncoding = "UTF-8"
		}
		reader = new InputStreamReader(bufferedUrlInputStream, (String)contentEncoding)

		def htmlCompiler = htmlProcessor.newXsltCompiler()
		def htmlExecutable = htmlCompiler.compile(new StreamSource(getClass().getClassLoader().getResourceAsStream("strip-html.xslt")))

		def htmlCleaner = htmlExecutable.load()
		htmlCleaner.initialContextNode = htmlDocumentBuilder.build(new StreamSource(reader))

		OutputStream serializerOs = new PipedOutputStream();
		InputStream serializerIs = new PipedInputStream(serializerOs);
		try {
			Serializer s = new Serializer()
			s.setOutputStream(serializerOs)
			htmlCleaner.setDestination(s)

			StreamStringReader streamStringReader = new StreamStringReader(serializerIs, 10 * 60 * 1000) // 10 minute timeout
			streamStringReader.start()
			htmlCleaner.transform()
			serializerOs.flush()
			serializerOs.close()
			synchronized(streamStringReader) {
				while(!streamStringReader.isClosed()) {
					streamStringReader.wait();
				}
			}
			html = streamStringReader.getString()
		} catch(IOException ex) {
			log.log(Level.WARN, "IOException: " + ex.getMessage())
			html = errorStr
		} catch(Exception ex) {
			log.log(Level.WARN, ex.getMessage())
			html = errorStr
		}
		finally {
			serializerOs.close()
			serializerIs.close()
		}
	} catch(FileNotFoundException ex) {
		log.log(Level.WARN, "File not found: " + ex.getMessage())
		html = errorStr
	} catch(ConnectException ex) {
		log.log(Level.WARN, "Connection exception: " + urlConnection.getURL() + " - " + ex.getMessage())
		html = errorStr
	} catch(IOException ex) {
		log.log(Level.WARN, "IOException: " + ex.getMessage())
		html = errorStr
	} catch(Exception ex) {
		log.log(Level.WARN, ex.getMessage())
		html = errorStr
	}
	finally
	{
		if(reader != null) reader.close()
		if(bufferedUrlInputStream !=  null)
			bufferedUrlInputStream.close();
		if(urlInputStream !=null)
			urlInputStream.close();
			
	}

	// Simple duplicate resolution: if fetching current object's link resulted in an error
	// and the same object was already previously fetched, then do not overwrite.
	if(html.equals(errorStr)) {
		File output = resultFactory.getFile("HealthMapAlertDocument_${alert.hashCode()}.xml");
		if (output.exists()) {
			log.log(Level.DEBUG, "Record ID: " + alert.hashCode() + " HTML Error | File exists | Not Overwriting");
			return;
		} else log.log(Level.DEBUG, "Record ID: " + alert.hashCode() + " HTML Error | File doesn't exist | Writing");
	} else log.log(Level.DEBUG, "Record ID: " + alert.hashCode() + " HTML OK | Writing file");

	// Standard Kea to serialize the parsed alert into PXML.
	def builder = createBuilder()
	builder.with
	{
		healthMapAlertDocument(id: "HealthMapAlert_" + alert.hashCode(),
				externalId: "HealthMapAlert_" + alert.hashCode(),
				label: alert.getTitle(),
				timestamp: alert.getDate(),
				latitude: location.getLat(),
				longitude: location.getLon())
		{
			media
			{
				text "${html}", title:alert.getTitle()
			}

			diseaseName alert.getDisease()
			description alert.getDescription()
			sourceName alert.getSource()

			healthMapRating
			{
				count alert.getRatingCount()
				rating alert.getRating()
			}

			title alert.getTitle()
			url finalUrl

			address
			{
				address_1 location.getPlaceName()
				country location.getCountry()
			}
		}
	}

	serialize builder, "HealthMapAlertDocument_${alert.hashCode()}.xml"
}

private URI constructURI(String authKey, Date startDate, Date endDate, String[] countryIds, String[] diseaseCategoryIds, String[] feedIds, String language) {
	if(authKey == null) {
		//Probably bad - this is an unchecked exception
		throw new IllegalArgumentException("No auth key - cannot query healthmap!");
	}
	StringBuilder uriBuilder = new StringBuilder("http://healthmap.org/HMapi.php?striphtml=true&auth=");
	uriBuilder.append(authKey);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
	if(startDate != null) {
		uriBuilder.append("&sdate=")
		uriBuilder.append(dateFormat.format(startDate))
	}
	if(endDate != null) {
		uriBuilder.append("&edate=")
		uriBuilder.append(dateFormat.format(endDate))
	}
	if(countryIds != null && countryIds.length > 0) {
		uriBuilder.append("&c=")
		uriBuilder.append(commaSeparatedString(countryIds))
	}
	if(diseaseCategoryIds != null && diseaseCategoryIds.length > 0) {
		uriBuilder.append("&d=")
		uriBuilder.append(commaSeparatedString(diseaseCategoryIds))
	}
	if(feedIds != null && feedIds.length > 0) {
		uriBuilder.append("&f=")
		uriBuilder.append(commaSeparatedString(feedIds))
	}
	if(language != null) {
		uriBuilder.append("&lang=")
		uriBuilder.append(lang)
	}
	return new URI(uriBuilder.toString())
}

private String commaSeparatedString(String[] values) {
	if(values == null || values.length <= 0) {
		return ""
	}
	StringBuilder builder = new StringBuilder()
	builder.append(values[0])
	for(int i = 1; i < values.length; i++) {
		builder.append(",")
		builder.append(values[i])
	}
	return builder.toString()
}






