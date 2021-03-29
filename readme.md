About
======

This is Java Containers - my own version of slim jar technology allowing to successfully fight the problem of big size of uber jars.

Recently a quite similar technology was implemented for Spring Boot applications, this project has different aims.

I was inspired by layered docker images (https://dzone.com/articles/docker-layers-explained), and decided to apply similar approach but for Java.

Overview
=========

Container is bidden to the local maven repository.
Before executing jar in container, the following flow of system checks and operations is performed

 
1) container uses jar coordinates to download pom file and detect the transitive dependencies - on this stage only local maven repository is used 
2) container uses sha1 control sums for already downloaded jars to be sure that all dependent jars are present 
3) on this stage if sync is perfect, container instantiates SystemProcess wrapper and run jar immediately
4) overwise container downloads all missed dependencies and refreshes the container inner work directory with the latest versions of jars
5) finally container instantiates SystemProcess wrapper and run jar

The typical command which container uses to run jar has the following look:

```
java -cp jar_to_run.jar;dependency_jar_1;dependency_jar_2;dependency_jar_3 main.class.name
```


Running
========

Build the whole project using standard command

```
mvn clean package
```
As a result several jar archives with code will be build

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

In order to run application inside container the container configuration json is needed:

```
{
   "workingDir":"path_to_working_directory",
   "artifactFQN":"org.containers:container-samples:1.0.0",
   "entryPoint":"org.containers.demo.SimpleApplication",
   "repositories":[
      {
         "repositoryId":"LOCAL",
         "url":"file:/home/user/.m2/repository"
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

Next execute the main demo class using the following command

```
java -jar ./container-engine/target/container-engine-1.0.0-shaded.jar <path to configuration.json>
```
which results in successful execution of application inside container:

```
(output truncated)

17:32:19.911 [pool-1-thread-1] INFO  org.containers.engine.SystemProcess -
17:32:19.911 [pool-1-thread-1] INFO  org.containers.engine.SystemProcess - 17:32:19.901 [main] INFO  o.containers.demo.SimpleApplication - 2 x 3 = 6.0
17:32:19.934 [main] INFO  org.containers.engine.JVMContainer - container b1c960c7-4e1b-4ccd-bb69-5cc69463782a finished
18:00:10.594 [main] INFO  org.containers.engine.SystemProcess - info {duration= 0.2900 sec, exit code=0, process=java -cp <jar paths> org.containers.demo.SimpleApplication, error message=null, pid=4156, started=Mon Mar 29 18:00:10 MSK 2021, finished=Mon Mar 29 18:00:10 MSK 2021}
18:00:10.601 [main] INFO  org.containers.boot.Booter - completed

```

Requirements
=============

* Java 8+
* Maven 3.6    

References
===========

[1] https://maven.apache.org/resolver/ (NOTE: the project Eclipse Aether is not maintained anymore, this is the successor)



