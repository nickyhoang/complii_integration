#!/bin/bash
#
# Set JAVA_HOME if required
#
#export JAVA_HOME=
java -jar complii_integration-${project.version}.jar & echo $! > ./pid.file &