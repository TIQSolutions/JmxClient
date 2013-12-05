/*
 * Copyright 2013
 * Dan Häberlein
 * TIQ Solutions GmbH
 * Weißenfelser Str. 84
 * 04229 Leipzig, Germany
 *
 * info@tiq-solutions.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
/**
 * JMX Bean monitor class. 
 * Use de.tiq.jmx.MultipleJmxClients to easily connect to multiple jmx clients. 
 * Therefore you can use a file called jmx-clients.xml in your local working directory. 
 * 
 * Use de.tiq.jmx.JmxClient to connect to one specific jmx client.
 * You can use the -help option to get more help.
 *
 */
package de.tiq.jmx

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import javax.management.MBeanServerConnection
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import javax.net.ssl.HostnameVerifier;

import de.tiq.csv.CsvPrinter


class MultipleJmxClients{

	private static String DEFAULT_CLIENTS_FILE = "jmx-clients.xml"

	static class JmxHostPortData {
		String host
		String port
	}

	static main(args){
		def clients = []
		def xmlParser = new XmlParser().parseText(new File(DEFAULT_CLIENTS_FILE).getText('UTF-8'))
		xmlParser.each { node ->
			clients << new JmxHostPortData(host:node.get("host").text(), port:node.get("port").text())
		}
		for(client in clients){
			def outputfileName = client.host + "-" + client.port + "-jmx-statistics.csv"
			List<String> currentArgs = [
				args,
				"-h",
				client.host,
				"-p",
				client.port,
				"-of",
				outputfileName
			]
			JmxClient.main(currentArgs.flatten() as String[])
		}
	}
}

class JmxRemoteConnector {
	
	String host
	String port
	
	MBeanServerConnection getUnauthorizedMBeanServerConnection(){
		def jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://${host}:${port}/jmxrmi")
		return JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection()
	}
	
}

class JmxClient extends Thread {

	private static String HEADER = '';
	private static String USAGE = 'Usage: java (...) de.tiq.jmx.JmxClient -h <host> -p <port> -jf <path-to-jmx-file>';
	private static Long DEFAULT_INTERVALL = 1000L
	private static String DEFAULT_OUTPUT_FILE = "jmx_statistics.csv"

	static main(args){
		// TODO add shutdown hook for close to rmi connection (JmxClient instance)
		CliBuilder commandLineParser = createCliParser()
		def options = commandLineParser.parse(args)
		options.arguments()
		if(args.size() == 0 || options.help){
			commandLineParser.usage()
		} else if(options.host && options.port && options.jmx_file){
			def converter = new XmlToJmxBeanDataConverter(options.jmx_file); // == null ? "mbeans.xml" : args[0]
			List<JmxMBeanData> jmxData = converter.convertTo()
			def client = new JmxClient(options.host, options.port, jmxData, options.i ?: DEFAULT_INTERVALL);
			client.start()
			new CsvPrinter(options.of ?: DEFAULT_OUTPUT_FILE, jmxData.collect{it.getAttributes().join(",")}, client.getResultQueue(), options.c ?: "")
		} else {
			commandLineParser.usage()
		}
	}

	private static CliBuilder createCliParser() {
		CliBuilder commandLineParser = new CliBuilder(header:HEADER, usage:USAGE)
		commandLineParser.help('prints this help message')
		commandLineParser.h(args:1, argName:'host', longOpt:'host', 'the jmx host to address')
		commandLineParser.p(args:1, argName:'port', longOpt:'port', 'the jmx port to address')
		commandLineParser.i(args:1, argName:'intervall', longOpt:'intervall', 'the intervall of data emission, default is one second')
		commandLineParser.jf(args:1, argName:'jmxFile', longOpt:'jmx_file', 'the path to the jmx xml file, in which the processable attributes are specified')
		commandLineParser.of(args:1, argName:'outputFile', longOpt:'output_file', 'the path to the file, in which should contain the recorded data in csv format')
		commandLineParser.c(args:1, argName:'additionalComment', longOpt:'add_comment', 'A additional comment line in the output csv file header')
		return commandLineParser
	}

	private Long intervall
	private List<JmxMBeanData> retrievableMetrics
	private MBeanServerConnection mbeanConnection
	private volatile Boolean isInterupted = Boolean.FALSE

	BlockingQueue<List> resultQueue = new LinkedBlockingQueue<List>()

	JmxClient(List<JmxMBeanData> retrievableMetrics, Long intervall, String host, String port) throws IOException {
		this(retrievableMetrics, intervall, new JmxRemoteConnector(host:host, port:port).getUnauthorizedMBeanServerConnection())
	}
	
	JmxClient(List<JmxMBeanData> retrievableMetrics, Long intervall) throws IOException {
		this(retrievableMetrics, intervall, ManagementFactory.platformMBeanServer)
	}

	JmxClient(List<JmxMBeanData> retrievableMetrics, Long intervall, MBeanServerConnection mbeanConnection) throws IOException {
		this.intervall = intervall
		this.retrievableMetrics = retrievableMetrics
		this.mbeanConnection = mbeanConnection
	}

	@Override
	void run(){
		while(!isInterupted){
			sleep(intervall)
			def currentResult = []
			retrievableMetrics.each { currentJmxData ->
				currentResult << evaluateJmxMBeanData(currentJmxData)
			}
			resultQueue.add(currentResult.flatten())
		}
	}

	private List evaluateJmxMBeanData(JmxMBeanData currentJmxData) {
		def currentResult = []
		currentJmxData.attributes.each { attribute ->
			def currentDataSet = mbeanConnection.getAttribute(currentJmxData.associatedObjectName, attribute.name)
			currentResult << getDatasets(attribute, currentDataSet)
		}
		return currentResult
	}

	def getDatasets(Attribute attribute, currentDataSet) {
		def returnable = []
		if(CompositeData.class.isInstance(currentDataSet)){
			attribute.compositeKeys.each { key ->
				returnable << currentDataSet.get(key)
			}
		} else {
			returnable << currentDataSet
		}
		return returnable
	}

	@Override
	public void interrupt() {
		isInterupted = Boolean.TRUE
	}

}

