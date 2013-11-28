#!/bin/bash

mvn exec:java -Dexec.mainClass=de.tiq.jmx.MultipleJmxClients -Darguments='-jf,../test-classes/MBeansMultipleValue.xml' "$@"