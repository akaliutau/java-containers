package org.containers.demo;

import java.nio.file.Path;
import org.containers.engine.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A minimal application
 * 
 * Uses dependency in container-samples-deps module
 * 
 * This app demonstrates how jar app can be executed using Java Containers
 * 
 * NOTE: all project including its dependencies must be compiled and deployed to nexus before starting this demo
 * 
 * @author akaliutau

 */
public class SimpleContainerizedApplication {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleContainerizedApplication.class);

	
	public static void main(String[] args) {
		log.info("env: {}", System.getenv());
		Container container = new Container(Path.of(System.getenv("temp")));
		container.setEntryPoint("org.containers:container-samples:1.0.0", "org.containers.demo.SimpleApplication");
		container.build();
		container.run();
		log.info("completed");
	}

}
