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

import java.util.concurrent.LinkedBlockingQueue

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
