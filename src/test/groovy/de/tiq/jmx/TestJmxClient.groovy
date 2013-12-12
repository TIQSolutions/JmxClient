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
	
	private static final JmxMBeanData QUERY = new JmxMBeanData(
												associatedObjectName: new ObjectName("java.lang:type=Threading"),  
												attributes: [new Attribute(name : 'ThreadCount', compositeKeys : []), 
															 new Attribute(name : 'DaemonThreadCount', compositeKeys : [])
															 ])
	
	private static final JmxMBeanData QUERY_NEU = new JmxMBeanData(
													associatedObjectName: new ObjectName("java.lang:type=Memory"),  
													attributes: [new Attribute(name : 'HeapMemoryUsage', compositeKeys : ['init', 'max', 'used'])
																])
	
	private static final Long INTERVALL = 90L
	private static final String TESTPORT = "8909"
	
	
	@Parameters(name="calling constructor with params: QueryList, Intervall, {0}")
	public static List<Object[]> data() {
		def dummyJvmCreator = {
			def classPath = System.getProperty('java.class.path')
			DummyVmMain.startDummyJmxEnabledVM(classPath, TESTPORT)
		}
		return [ [ [], {}, QUERY_NEU ] as Object[],
				  [[ManagementFactory.platformMBeanServer], {}, QUERY] as Object[],
				  [ ["localhost", TESTPORT], dummyJvmCreator, QUERY ] as Object[] 
				] 
	}
	
	@Parameter(0)
	public List constructorParams
	
	@Parameter(1)
	public Closure callBeforeTest
	
	@Parameter(2)
	public JmxMBeanData query
	
	
	@Test
	public void testMBeanConnection(){
		callBeforeTest.call()
		def testable = new JmxClient([query], INTERVALL, *constructorParams)
		testable.start()
		Thread.sleep(1000)
		testable.interrupt()
		assert (8..10).containsWithinBounds(testable.resultQueue.size()) 	
	}
	
}
