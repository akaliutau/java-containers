package containers.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.library.MathUtils;

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
public class SimpleApplication {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleApplication.class);

	
	public static void main(String[] args) {
		double product = MathUtils.multiply(2.0, 3.0);
		log.info("this is a demo application. Uses dependencies specified in module container-samples-deps");
		log.info("2 x 3 = {}", product);
	}

}
