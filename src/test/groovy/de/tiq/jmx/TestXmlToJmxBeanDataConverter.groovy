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
