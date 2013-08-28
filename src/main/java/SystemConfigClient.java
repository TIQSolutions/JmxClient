import java.io.IOException;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class SystemConfigClient {

    public static final String HOST = "localhost";
    public static final String PORT = "1234";


    public static void main(String[] args) throws Exception {
        JMXServiceURL url =
            new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT + "/jmxrmi");
//        	new JMXServiceURL("service:jmx:rmi://127.0.0.1:10103/jndi/rmi://127.0.0.1:10103/jmxrmi");
        //service:jmx:rmi://127.0.0.1:1099/jndi/rmi://127.0.0.1:1099/jmxrmi
        
        
        JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
        final MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
        //ObjectName should be same as your MBean name
        final ObjectName mbeanName = new ObjectName("java.lang:type=Threading");

        //Get MBean proxy instance that will be used to make calls to registered MBean
        final ThreadMXBean mxBean = MBeanServerInvocationHandler.newProxyInstance(mbeanServerConnection, mbeanName, ThreadMXBean.class, true);
        
        Thread printThread = new Thread(){
        	public void run() {
        		while(!isInterrupted()){
        			if(System.currentTimeMillis() % 1000 == 0)
						try {
							System.out.println(mbeanServerConnection.getAttribute(mbeanName, "ThreadCount"));
						} catch (Exception e) {
							e.printStackTrace();
						}
        		}
        	};
        };
        printThread.start();
        System.in.read();
        printThread.interrupt();
        printThread.join();
        //close the connection
        jmxConnector.close();
    }

}
