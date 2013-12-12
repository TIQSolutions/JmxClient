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

import javax.management.ObjectName

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class TestXmlToJmxBeanDataConverter {

	private static String FILE_PATH_PREFIX = "./"
	
	@Parameters(name="parsing xml file {0} to JmxMBeanData")
	public static List<Object[]> data() {
		
		return [ ["MBeans.xml", createSimpleFixture()] as Object[],
				  ["MBeans_Composite.xml", createCompositeAttributeFixture()] as Object[]
				]
	}
	
	def static createSimpleFixture(){
		def objectName = new ObjectName("java.lang:type=Threading")
		def attributeList = [new Attribute(name: "ThreadCount", compositeKeys: [])]
		return new JmxMBeanData(associatedObjectName:objectName, attributes:attributeList)
	}
	
	def static createCompositeAttributeFixture(){
		def objectName = new ObjectName("java.lang:type=Memory")
		def attributeList = [new Attribute(name: "HeapMemoryUsage", compositeKeys: ['init', 'max', 'used'])]
		return new JmxMBeanData(associatedObjectName:objectName, attributes:attributeList)
	}
	
	@Parameter(0)
	public String filename
	
	@Parameter(1)
	public JmxMBeanData expected
		
	public XmlToJmxBeanDataConverter testable 
	
	@Before
	public void before() {
		testable = new XmlToJmxBeanDataConverter(FILE_PATH_PREFIX + filename)
	}
	
	@Test
	public void testParsing() throws Exception {
		assert expected == testable.convertTo()[0]
	}
}
