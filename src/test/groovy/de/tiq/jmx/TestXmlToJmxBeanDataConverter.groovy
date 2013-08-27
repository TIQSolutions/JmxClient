package de.tiq.jmx

import groovy.util.GroovyTestCase
import de.tiq.jmx.JmxMBeanData
import de.tiq.jmx.XmlToJmxBeanDataConverter;
import javax.management.ObjectName

class TestXmlToJmxBeanDataConverter extends GroovyTestCase {

	private static String FILE_PATH_PREFIX = "src/test/resources" 
		
	public void testSingleObjectCreation() throws Exception {
		XmlToJmxBeanDataConverter testable = new XmlToJmxBeanDataConverter(FILE_PATH_PREFIX + "/MBeans.xml")
		assert new JmxMBeanData("java.lang.management.ThreadMXBean", new ObjectName("java.lang:type=Threading"), ["ThreadCount"]) == testable.convertTo()[0]
	}
	
//	public void testMultipleObjectCreation() throws Exception {
//		XmlToJmxBeanDataConverter testable = new XmlToJmxBeanDataConverter(FILE_PATH_PREFIX + "/MBeansMultipleValue.xml")
//		assert new JmxMBeanData("ThreadMXBean", new ObjectName("java.lang:type=Threading"), ["getThreadCount"]) == testable.convertTo()[0]
//	}
}
