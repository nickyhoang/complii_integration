# GBST Complii Integration

This project contains the GBST Complii Integration processor which is a spring boot application.

It currently contains the following jobs:

1. Order Job
2. Account Job
3. Holding Job

**Presequisite**

1. The trigger for shares table must have been executed (against the shares database), CD-1739-sharesTrigger.sql, prior to running this Complii Integration module.
2. The configuration items has to be initialized by executing CD-1739.sql file against FO database prior to running this Complii Integration module

**Running Batch Process**

The following command:

java -jar complii_integration-<VERSION>.jar

This will produce log files into a **log** directory relative to where the project has been run.

**Configuration**

All configuration parameters are contained in the application.yml file.  This file can be placed **config** directory relative
to where the batch process is run. 

The following is the list available parameters:

1. complii.order.service.job.fixed.delay.ms
2. complii.account.service.job.fixed.delay.ms
3. complii.holding.service.job.fixed.delay.ms

The datasource - contains the database connection parameters is configured in the default application.properties


**Development Notes**


**Release Kit**

Build the project by using the maven commend *mvn clean install*, this will place a tar.gz in the target directory called complii_integration-<VERSION_NUMBER>-bin.tar.gz.  Perform the following steps to install the kit:

1. Copy release kit tar to target directory.
2. Unpack release kit using the command **tar -zxvf complii_integration-<VERSION_NUMBER>-bin.tar.gz**.
3. This will create a directory called complii_integration-<VERSION_NUMBER> which contains the executable jar, config directory, start and stop scripts.
4. Alter the configuration file **application.yml** as required.
5. Create output directories as per configuration.
5. Alter the logging configuration file **logback.xml** as required.
6. Execute the **start.sh* script to ensure the batch application runs as expected.

 
