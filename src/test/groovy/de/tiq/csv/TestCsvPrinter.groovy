package de.tiq.csv

import java.util.concurrent.LinkedBlockingQueue

import org.mortbay.jetty.security.SSORealm;

class TestCsvPrinter extends GroovyTestCase {

	private static String TEST_FILE_DEST = "./target/test-classes/csv-data-testfile"
	
	public void testSimpleFileCreation() throws Exception {
		def resultListHandle = new LinkedBlockingQueue()
		def headerFixture = ["headerA","headerB"]
		def testable = new CsvPrinter(TEST_FILE_DEST, headerFixture, resultListHandle)
		def resultFile = new File(TEST_FILE_DEST)
		assertEquals(headerFixture.join(","), resultFile.getText().split(/\n/)[1])
		resultListHandle.put(["a","b"])
		resultListHandle.put(["c","d"])
		Thread.sleep(100L)
		def finalResult = resultFile.getText().split(/\n/)
		assertEquals(4, finalResult.size())
		assertEquals(headerFixture.join(","), finalResult[1])
		assertEquals("a,b", finalResult[2])
		assertEquals("c,d", finalResult[3])
	}
}
