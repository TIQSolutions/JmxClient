/*
 * JMX Bean monitor script. 
 * args[0] host of JMX server programm
 * args[1] port of JMX server programm
 *
 */

package de.tiq.jmx

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue

import de.tiq.csv.CsvPrinter
import groovy.transform.PackageScope;

import javax.management.MBeanServerConnection
import javax.management.MBeanServerInvocationHandler
import javax.management.ObjectName
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

class Main{
	static main(args){
		def outputFileName = "jmx-statistics.csv"
		Long intervall = 1000L
		if(args.size() == 4){
			 intervall = Long.parseLong(args[3])
		}
		if(args.size() == 5){
			outputFileName = args[4]
		}
		def converter = new XmlToJmxBeanDataConverter(args[0]); // == null ? "mbeans.xml" : args[0]
		List<JmxMBeanData> jmxData = converter.convertTo()
		def client = new JmxClient(args[1], args[2], jmxData, intervall);	
		client.start()
		new CsvPrinter(outputFileName, jmxData.collect{it.getAttributes().join(",")}, client.getResultQueue())
	}
}

class JmxClient extends Thread {

	private Long intervall
	private List<JmxMBeanData> retrievableMetrics
	private MBeanServerConnection mbeanConnection
	private JMXServiceURL jmxUrl
	
	BlockingQueue<List> resultQueue = new LinkedBlockingQueue<List>()

	JmxClient(String host, String port, List<JmxMBeanData> retrievableMetrics, Long intervall) throws IOException {
		this.intervall = intervall
		this.retrievableMetrics = retrievableMetrics
		jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi")
	}

	@Override
	void run(){
		mbeanConnection = JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
		while(true){
			if(System.currentTimeMillis() % intervall == 0){
				def currentResult = []
				retrievableMetrics.each { currentJmxData -> 
					currentJmxData.attributes.each { attribute ->
						currentResult << mbeanConnection.getAttribute(currentJmxData.associatedObjectName, attribute)
					}
				}
				resultQueue.add(currentResult)
				System.out.println(currentResult)
			}
		}
	}

	void close(){
		jmxConnector.close()
	}
}

