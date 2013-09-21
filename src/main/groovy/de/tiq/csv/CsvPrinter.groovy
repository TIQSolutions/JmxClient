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
package de.tiq.csv

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue

class CsvPrinter extends Thread {
	
	private static SimpleDateFormat FORMATER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss S")
	private static String SEPARATOR = ','
	private static String addtionalComments
	
	private File outputFile
	private List<String> headerInfo
	private BlockingQueue<List<String>> writableResults

	CsvPrinter(String pathToOutputFile, List<String> headerInfo, BlockingQueue<List<String>> writableResults, String additionalComments=""){
		this.addtionalComments = additionalComments
		this.writableResults = writableResults
		this.headerInfo = headerInfo
		initOutputFile(pathToOutputFile)
		this.setDaemon(true)
		this.start()
	}
	
	def initOutputFile(String pathToOutputFile){
		this.outputFile = new File(pathToOutputFile)
		if(!outputFile.exists())
			outputFile.createNewFile()
		printHeader()
	}
	
	def printHeader(){
		outputFile.append("# generated from JMXClient application at " + FORMATER.format(new Date()))
		outputFile.append("\n")
		if(!addtionalComments.isEmpty()){
			outputFile.append("# " +addtionalComments)
			outputFile.append("\n")
		}
		outputFile.append(headerInfo.join(SEPARATOR));
		outputFile.append("\n")
	}
	
	@Override
	public void run() {
		while(true){
			def currentResult = writableResults.take()
			outputFile.append(currentResult.join(SEPARATOR)) 
			outputFile.append("\n")
		}
	}
}
