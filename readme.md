# JMX CSV Writer Client

This project is used to extract arbitrary data from the JMX interface into csv files. 

## Getting Started

There are two possible main classes: 

```java
de.tiq.jmx.JmxClient
```
The JmxClient is a command line utility providing simple cli-parameters to extract data from the the JMX interface. The usage specification looks like this:
<code>
usage: Usage: java (...) de.tiq.jmx.JmxClient -h <host> -p <port> -jf
              <path-to-jmx-file>
 -c,--add_comment <additionalComment>   A additional comment line in the
                                        output csv file header
 -h,--host <host>                       the jmx host to address
 -help                                  prints this help message
 -i,--intervall <intervall>             the intervall of data emission,
                                        default is one second
 -jf,--jmx_file <jmxFile>               the path to the jmx xml file, in
                                        which the processable attributes
                                        are specified
 -of,--output_file <outputFile>         the path to the file, in which
                                        should contain the recorded data
                                        in csv format
 -p,--port <port>                       the jmx port to address
</code>
As you can see, the JmxClient needs at least three parameters. 
- the host address, where to connect at
- the port on which the jmx server emits data
- a jmx_file, which is basicly just a xml file, containing the information about which values are extracted

A jmx file could be named without any restrictions. 
It's content should look like this:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmxData>
	<JmxMBeanData>
		<associatedObjectName>java.lang:type=Threading</associatedObjectName>
		<attributes>
			<attribut>
				<name>ThreadCount</name>
			</attribut>
		</attributes>
	</JmxMBeanData>
</jmxData>
```
A you can see, you can specify many ore one attribute(s) for each available associatedObjectName. 
Which attributes are available differs of course from every associatedObject.
If an attribute is an instance of 
In the case of the example ThreadMXBean, see http://docs.oracle.com/javase/6/docs/api/java/lang/management/ThreadMXBean.html for details.

```java
de.tiq.jmx.MultipleJmxClients
```

This main class just parses a file called "jmx-clients.xml", which currently should be located in your current working directory. It contains simple address information about multiple jmx clients. 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmx-clients>
	<client>
		<host>localhost</host>
		<port>1234</port>
	</client>
</jmx-clients>
```
The main class will start an instance of a jmx client for each specified client tag. 
All other command line options are just delegated to the JmxClient-instances, like the jmx_file from above.
An example call could look like: 
```bash
-java -cp ... de.tiq.jmx.MultipleJmxClients -jf ./path/to/my/jmx_file.xml
```
Any output file will follow the pattern: 
```
<host>-<port>-jmx-statistics.csv
```
### Prerequisites
Maven is used to build the project. 
You need to have maven installed on your computer in order to compile and run the source code. 

#### Build the project
This project is built with maven. You should install maven on your operating system to be able to build the project. 
Use 'mvn package' in the root directory of the project (the project's POM file is located there). This will create a jar of this project in the "target" folder. 

## Colophon		

### Company

This project is developed by TIQ Solutions GmbH, a german enterprise for data quality management.
You can contact us: info@tiq-solutions.de 

### License 

The project is licensed under terms of Apache License, Version 2.0.

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

