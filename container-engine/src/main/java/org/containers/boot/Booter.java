package org.containers.boot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.containers.engine.JVMContainer;
import org.containers.model.ContainerDescriptor;
import org.containers.model.RepositoryDescriptor;
import org.containers.repository.RepositoryFactory;
import org.containers.util.FileUtils;
import org.containers.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point to run container
 * 
 * Fully configurable through the configuration in json files 
 * @author akaliutau
 *
 */
public class Booter {
	private static final Logger log = LoggerFactory.getLogger(Booter.class);

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
	    log.info("Argument count: " + args.length);
	    String current = System.getProperty("user.dir");
	    
	    log.info("Current working directory in Java : {}", current);
	    if (args.length < 1) {
	    	log.info("usage: java -jar java-containers.jar configuration.json");
	    }
	    log.info("loading configuration from {}", args[0]);
	    
	    ContainerDescriptor res = JsonUtils.fromJson(Paths.get(args[0]), ContainerDescriptor.class);
	    Path workingDir = res.getWorkingDir() == null ? FileUtils.getCurrentWorkingDirectory() : Paths.get(res.getWorkingDir());
		JVMContainer container = new JVMContainer(workingDir);
		container.setArtifactPath(res.getArtifactFQN());
		container.setEntryPoint(res.getEntryPoint());
		container.setExclusions(res.getDropArtifacts());
		
		// using default repositories:
		if (res.getRepositories() != null) {
			for (RepositoryDescriptor repo : res.getRepositories()){
				container.addRemoteRepository(RepositoryFactory.createRepository(repo));
			}
		}

		container.build();
		container.run();
		log.info("exit container");

	}

}
