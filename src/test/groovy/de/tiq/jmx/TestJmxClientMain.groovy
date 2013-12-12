package de.tiq.jmx

class TestJmxClientMain extends GroovyTestCase {

	public static final String TEST_PORT = '8909'
	
	public void testJmxClientMainMethod(){
		def resultFileName = "jmxstatistics-${UUID.randomUUID().getMostSignificantBits()}.csv"
		DummyVmMain.startDummyJmxEnabledVM(System.getProperty('java.class.path'), TEST_PORT)
		JmxClient.main(['-h', 'localhost', '-p', TEST_PORT, '-jf', new File('./MBeans_Composite.xml').getAbsolutePath(), '-of', resultFileName] as String[])
		def resultFile = new File(resultFileName)
		def lines = resultFile.text.split('\n')
		assert 'init,max,used' == lines[1]
		assert 3 == lines[2].split(JmxClient.SEPARATOR).size()
		assert 3 == lines[3].split(JmxClient.SEPARATOR).size()
	}
}
