About
======

This is Java Containers - my own version of slim jar technology allowing to successfully fight the problem of big size of uber jars.

Recently a quite similar technology was implemented for Spring Boot applications. 
This project was started having in mind  slightly different aims.

I was inspired by layered docker images technology (https://dzone.com/articles/docker-layers-explained) used to make the distribution and running docker containers maximum fast and efficient, and I decided to apply similar approach but for Java application.


Overview
=========

The container could be bidden to the local maven repository, to Nexus or to the central maven repository.
In either way the necessary artifacts are collected in local directory

Before executing jar in container, the following flow of system checks and operations is performed

 
1) container uses jar coordinates to download pom file and detect the transitive dependencies - on this stage only local maven repository is used 
2) container uses sha1 control sums for already downloaded jars to be sure that all dependent jars are present 
3) on this stage if sync is perfect, container instantiates SystemProcess wrapper and run jar immediately; the classpath is compiled on the basis of all found jars
4) overwise container downloads all missed dependencies and refreshes the container inner work directory with the latest versions of jars
5) finally container instantiates SystemProcess wrapper and run jar

The typical command which container uses to run jar has the following look:

```
java -cp jar_to_run.jar;dependency_jar_1;dependency_jar_2;dependency_jar_3 main.class.name
```

This project contains three modules. 

## Container-engine

This module contains the core logic of container engine

## Container-samples

This module contains the simple java application with 1 dependency

## Container-samples-deps

This module contains 1 dependency (library JAR) for sample application



Running
========

Build the whole project using standard command

```
mvn clean install package  -DcreateChecksum=true
```
As a result several jar archives with code will be build and installed into local .m2 repository

Note the flag createChecksum added into the command - this is needed in order to generate sha1 sums for artifacts (one can omit this, but this will lead to non-fatal warnings like org.eclipse.aether.transfer.ChecksumFailureException: Checksum validation failed, no checksums available)


One can execute the main demo class using the following command

```
java -cp container-samples-1.0.0.jar org.containers.demo.SimpleApplication
```
which results in expected error (lack of necessary dependencies on classpath):

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
        at org.containers.demo.SimpleApplication.<clinit>(SimpleApplication.java:22)
Caused by: java.lang.ClassNotFoundException: org.slf4j.LoggerFactory
  ...
 (further stack trace omitted for brevity)
```

In order to run application inside container the container configuration json is needed (a sample can be found in .workingDir directory):

```
{
   "workingDir":"path_to_working_directory",                  <--  can be omitted; defaulting in current directory
   "artifactFQN":"org.containers:container-samples:1.0.0",
   "entryPoint":"org.containers.demo.SimpleApplication",
   "repositories":[
      {
         "repositoryId":"LOCAL",
         "url":"file:/home/user/.m2/repository"               <--  can be omitted; engine will try to figure out exact location using system env variables
      },
      {
         "repositoryId":"CENTRAL",
         "url":"https://repo.maven.apache.org/maven2/"
      }
   ]
}
```
Note, that resolver tries to find the artifact in the local repository first (default policy is set to "Never update locally cached data")

Note also that there possibly will be need to update permissions for .m2 repository

Not all properties are mandatory. For example, one can omit workingDir property which results in using default settings (see model class ContainerDescriptor for details)


Next execute the main demo class using the following command

```
java -jar ./container-engine/target/container-engine-1.0.0-shaded.jar <path to configuration.json>
```

For example one can use the configuration from .workingDir:

```
java -jar ./container-engine/target/container-engine-1.0.0-shaded.jar .workingDir/configuration.json
```

which results in successful execution of application inside container:

```
(output truncated)

org.containers:container-samples:jar:1.0.0
+- ch.qos.logback:logback-classic:jar:1.2.3 [compile]
|  +- ch.qos.logback:logback-core:jar:1.2.3 [compile]
|  \- org.slf4j:slf4j-api:jar:1.7.25 [compile]
\- org.containers:container-samples-deps:jar:1.0.0 [compile]

10:26:35.733 [main] INFO  org.containers.engine.JVMContainer - command line: [java, -cp, <classpath jars>, org.containers.demo.SimpleApplication]
10:26:35.735 [main] INFO  org.containers.engine.JVMContainer - container 8eeae794-0db9-4801-b0fd-2d65bafbc428 started
10:26:35.909 [pool-2-thread-1] INFO  org.containers.engine.SystemProcess - 10:26:35.905 [main] INFO org.containers.demo.SimpleApplication - 2 x 3 = 6.0
10:26:35.922 [main] INFO  org.containers.engine.JVMContainer - container 8eeae794-0db9-4801-b0fd-2d65bafbc428 finished
10:26:35.923 [main] INFO  org.containers.engine.SystemProcess - info {duration= 0.1770 sec, exit code=0, process=java -cp <jars> org.containers.demo.SimpleApplication, error message=null, pid=6084, started=Tue Mar 30 10:26:35 MSK 2021, finished=Tue Mar 30 10:26:35 MSK 2021}
10:26:35.923 [main] INFO  org.containers.boot.Booter - completed

```

In logs one can find that engine correctly detected and built the dependency tree for artifact <code> org.containers:container-samples:1.0.0</code> specified in configuration:

The full tree is a bit longer; extra dependencies can be dropped using drop field in configuration

```
org.containers:container-samples:jar:1.0.0
+- org.containers:container-engine:jar:1.0.0 [compile]
|  +- org.apache.maven.resolver:maven-resolver:pom:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-api:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-spi:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-util:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-impl:jar:1.6.2 [compile]
|  |  +- org.apache.commons:commons-lang3:jar:3.8.1 [compile]
|  |  \- org.slf4j:slf4j-api:jar:1.7.30 [compile]
|  +- org.apache.maven.resolver:maven-resolver-connector-basic:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-transport-classpath:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-transport-file:jar:1.6.2 [compile]
|  +- org.apache.maven.resolver:maven-resolver-transport-http:jar:1.6.2 [compile]
|  |  +- org.apache.httpcomponents:httpclient:jar:4.5.12 [compile]
|  |  |  \- commons-codec:commons-codec:jar:1.11 [compile]
|  |  +- org.apache.httpcomponents:httpcore:jar:4.4.13 [compile]
|  |  \- org.slf4j:jcl-over-slf4j:jar:1.7.30 [runtime]
|  +- org.apache.maven:maven-resolver-provider:jar:3.6.0 [compile]
|  |  +- org.apache.maven:maven-model:jar:3.6.0 [compile]
|  |  +- org.apache.maven:maven-repository-metadata:jar:3.6.0 [compile]
|  |  +- org.codehaus.plexus:plexus-utils:jar:3.1.0 [compile]
|  |  \- javax.inject:javax.inject:jar:1 [compile]
|  +- org.apache.maven:maven-model-builder:jar:3.6.0 [compile]
|  |  +- org.codehaus.plexus:plexus-interpolation:jar:1.25 [compile]
|  |  +- org.codehaus.plexus:plexus-component-annotations:jar:1.7.1 [compile]
|  |  +- org.apache.maven:maven-artifact:jar:3.6.0 [compile]
|  |  \- org.apache.maven:maven-builder-support:jar:3.6.0 [compile]
|  +- org.apache.maven.resolver:maven-resolver-transport-wagon:jar:1.6.2 [compile]
|  |  \- org.apache.maven.wagon:wagon-provider-api:jar:3.4.0 [compile]
|  \- ch.qos.logback:logback-classic:jar:1.2.3 [compile]
|     \- ch.qos.logback:logback-core:jar:1.2.3 [compile]
\- org.containers:container-samples-deps:jar:1.0.0 [compile]
```

Requirements
=============

* Java 8+
* Maven 3.6    

References
===========

[1] https://maven.apache.org/resolver/ (NOTE: the project Eclipse Aether is not maintained anymore, this is the successor)



