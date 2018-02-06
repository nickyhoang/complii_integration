#!/bin/bash
#
# Set JAVA_HOME if required
#
#export JAVA_HOME=

# Set JMX parameters if needed
# -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=<PORT_NUMBER> -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=<HOST'S_IP>
java -jar complii_integration-${project.version}.jar & echo $! > ./pid.file &


