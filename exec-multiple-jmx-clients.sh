#!/bin/bash

java -cp ".;"$(cd ../.. && mvn dependency:build-classpath | grep -E "(C:\\.*\n?)") de.tiq.jmx.MultipleJmxClients -jf ../test-classes/MBeansMultipleValue.xml
