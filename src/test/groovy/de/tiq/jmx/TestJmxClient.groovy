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
package de.tiq.jmx

import java.lang.management.ManagementFactory
import javax.management.ObjectName
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class TestJmxClient {
	
	private static final JmxMBeanData QUERY = new JmxMBeanData(associatedObjectName: new ObjectName("java.lang:type=Threading"),  attributes: ['ThreadCount', 'DaemonThreadCount'] )
	private static final Long INTERVALL = 90L
	
	private static final String TESTPORT = "8909"
	
	@Parameters(name="calling constructor with params: QueryList, Intervall, {0}")
	public static List<Object[]> data() {
		def dummyJvmCreator = {
			def classPath = System.getProperty('java.class.path')
			def jmxremote = 'com.sun.management.jmxremote'
			"""java -D${jmxremote}\
			   -D${jmxremote}.authenticate=false\
			   -D${jmxremote}.ssl=false\
			   -D${jmxremote}.port=${TESTPORT}\
			   -cp ${classPath} de.tiq.jmx.DummyVmMain"""
           .execute()
			Thread.sleep(1000L)
		}
		return [ [ [], {} ] as Object[],
				  [[ManagementFactory.platformMBeanServer], {}] as Object[],
				  [ ["localhost", TESTPORT], dummyJvmCreator  ] as Object[] 
				] 
	}
	
	@Parameter(0)
	public List constructorParams
	
	@Parameter(1)
	public Closure callBeforeTest
	
	
	@Test
	public void testMBeanConnection(){
		callBeforeTest.call()
		def testable = new JmxClient([QUERY], INTERVALL, *constructorParams)
		testable.start()
		Thread.sleep(1000)
		testable.interrupt()
		assert (9..11).containsWithinBounds(testable.resultQueue.size()) 	
	}
	
}