package containers.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A minimal application without any explicit dependencies
 * 
 * Note, that all dependent classes will be added to classpath anyway, because container engine functionality analyzes only pom
 * 
 * This app demonstrates how jar app can be executed using Java Containers
 * 
 * @author akaliutau
 * 
 */
public class MinimalApplication {
	
	private static final Logger log = LoggerFactory.getLogger(MinimalApplication.class);

	
	public static void main(String[] args) {
		log.info("this is a demo application");
	}

}
