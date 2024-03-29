About
======

This is Java Containers - my own version of slim jar technology allowing to successfully fight the problem of big size of uber jars.

Recently a quite similar technology was implemented for Spring Boot applications (https://github.com/akaliutau/spring-boot-thin-launcher). 
This project was started having in mind  slightly different aims.

My project was started having in mind  slightly different aims - I had a wish to create a general-purpose technology.
I was inspired by layered docker images technology (https://dzone.com/articles/docker-layers-explained) used to make the distribution and running docker containers maximum fast and efficient, and I decided to apply similar approach but for Java application. Among other influences I can name Virtual Environment technology in Python world.

The main idea is to separate business logic classes and application dependencies. All application's  dependencies are held in local storage which plays the role of cache, and instead of distribution of full jar with all dependencies only classes containing business logic are distributed.

This technology has many potential applications. For example, the decreasing of jar size could shorten the spin up time for processes running in distributed system (such as Spark or Storm to name a few), in implementing engine for serverless platform and so on.

Overview
=========

The container could be bidden to the local maven repository, to Nexus or to the central maven repository.
In either way the necessary artifacts are collected in local directory (by default this is a local-repo folder created in working directory, but can be overriden in configuration)

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

Currently only one entry point is supported - the static void main(String[] args) method in jar (the standard entry point for java apps)

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
java -cp ./container-samples/target/container-samples-1.0.0.jar containers.demo.SimpleApplication
```
which results in expected error (lack of necessary dependencies on classpath):

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
        at containers.demo.SimpleApplication.<clinit>(SimpleApplication.java:22)
Caused by: java.lang.ClassNotFoundException: org.slf4j.LoggerFactory
  ...
 (further stack trace omitted for brevity)
```

In order to run application inside container the container configuration json is needed (a sample can be found in .workingDir directory):

```
{
   "workingDir":"path_to_working_directory",                  <--  can be omitted; defaulting in current directory
   "artifactFQN":"containers:container-samples:1.0.0",
   "entryPoint":"containers.demo.SimpleApplication",
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

containers:container-samples:jar:1.0.0
+- ch.qos.logback:logback-classic:jar:1.2.3 [compile]
|  +- ch.qos.logback:logback-core:jar:1.2.3 [compile]
|  \- org.slf4j:slf4j-api:jar:1.7.25 [compile]
\- containers:container-samples-deps:jar:1.0.0 [compile]

10:26:35.733 [main] INFO  containers.engine.JVMContainer - command line: [java, -cp, <classpath jars>, containers.demo.SimpleApplication]
10:26:35.735 [main] INFO  containers.engine.JVMContainer - container 8eeae794-0db9-4801-b0fd-2d65bafbc428 started
10:26:35.766 [pool-2-thread-1] INFO  containers.engine.SystemProcess - 10:26:35.735 [main] INFO containers.demo.SimpleApplication - this is a demo application. Uses dependencies specified in module container-samples-deps
10:26:35.909 [pool-2-thread-1] INFO  containers.engine.SystemProcess - 10:26:35.905 [main] INFO containers.demo.SimpleApplication - 2 x 3 = 6.0
10:26:35.922 [main] INFO  containers.engine.JVMContainer - container 8eeae794-0db9-4801-b0fd-2d65bafbc428 finished
10:26:35.923 [main] INFO  containers.engine.SystemProcess - info {duration= 0.1770 sec, exit code=0, process=java -cp <jars> containers.demo.SimpleApplication, error message=null, pid=6084, started=Tue Mar 30 10:26:35 MSK 2021, finished=Tue Mar 30 10:26:35 MSK 2021}
10:26:35.923 [main] INFO  containers.boot.Booter - completed

```
What happend here? 

1) First, the java container was instantiated
2) Container engine used pom for <code>containers:container-samples:1.0.0</code> artifact to figure out all dependencies
3) Container downloaded all dependencies into the local working repository, by default it creates a directory /local-repo. note in this case the local .m2 repository was used, so the the spin up time was really small 
4) Code in jar was executed with all detected dependencies which have been passed to the classpath

 

In logs one can find that engine correctly detected and built the dependency tree for artifact <code> containers:container-samples:1.0.0</code> specified in configuration:
```
containers:container-samples:jar:1.0.0
+- ch.qos.logback:logback-classic:jar:1.2.3 [compile]
|  +- ch.qos.logback:logback-core:jar:1.2.3 [compile]
|  \- org.slf4j:slf4j-api:jar:1.7.25 [compile]
\- containers:container-samples-deps:jar:1.0.0 [compile]
```

The full tree is a bit longer; extra dependencies can be dropped using dropArtifacts field in configuration

```
containers:container-samples:jar:1.0.0
+- containers:container-engine:jar:1.0.0 [compile]
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
\- containers:container-samples-deps:jar:1.0.0 [compile]
```

Requirements
=============

* Java 8+
* Maven 3.6    

References
===========

[1] https://maven.apache.org/resolver/ (NOTE: the project Eclipse Aether is not maintained anymore, this is the successor)



