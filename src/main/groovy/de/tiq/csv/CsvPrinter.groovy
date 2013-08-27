package de.tiq.csv

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue

class CsvPrinter extends Thread {
	
	private static SimpleDateFormat FORMATER = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss S")
	private static String SEPARATOR = ','
	
	private File outputFile
	private List<String> headerInfo
	private BlockingQueue<List<String>> writableResults

	CsvPrinter(String pathToOutputFile, List<String> headerInfo, BlockingQueue<List<String>> writableResults){
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
