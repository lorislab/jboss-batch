Jboss batch for the EAP6.4 ${project.version} - bacis
==================
 
- Fork Wilfdly ${version.wildfly.extension}

=== 1. Installation

Unzip under the jboss directory.

=== 2. Configuration

Add the extension in the standalone.xml:
<extensions>
    ...
    <extension module="org.wildfly.extension.batch"/>
</extensions>

Create the subsystem batch:
<profile>
    ...
    <subsystem xmlns="urn:jboss:domain:batch:1.0">
        <job-repository>
            <in-memory>
        </job-repository>
        <thread-pool>
            <max-threads count="10"/>
            <keepalive-time time="30" unit="seconds"/>
        </thread-pool>
    </subsystem>
</profile>

Update the JAVAEE module and add the batch API:

modules/system/layers/base/javaee/api/main/module.xml:
<dependencies>
    ...
    <module name="javax.batch.api" export="true"/>
    ...
</dependencies>