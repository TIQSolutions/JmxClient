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

import groovy.util.GroovyTestCase
import de.tiq.jmx.JmxMBeanData
import de.tiq.jmx.XmlToJmxBeanDataConverter;
import javax.management.ObjectName

class TestXmlToJmxBeanDataConverter extends GroovyTestCase {

	private static String FILE_PATH_PREFIX = "src/test/resources" 
		
	public void testSingleObjectCreation() throws Exception {
		XmlToJmxBeanDataConverter testable = new XmlToJmxBeanDataConverter(FILE_PATH_PREFIX + "/MBeans.xml")
		assert new JmxMBeanData(associatedObjectName:new ObjectName("java.lang:type=Threading"), attributes:["ThreadCount"]) == testable.convertTo()[0]
	}
	
}
