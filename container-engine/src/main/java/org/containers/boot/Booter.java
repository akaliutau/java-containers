package org.containers.boot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.containers.engine.JVMContainer;
import org.containers.model.ContainerDescriptor;
import org.containers.model.RepositoryDescriptor;
import org.containers.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Booter {
	private static final Logger log = LoggerFactory.getLogger(Booter.class);

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
	    log.info("Argument count: " + args.length);
	    if (args.length < 1) {
	    	log.info("usage: java -jar java-containers.jar configuration.json");
	    }
	    ContainerDescriptor res = JsonUtils.fromJson(Paths.get(args[0]), ContainerDescriptor.class);
	    Path workingDir = Paths.get(res.getWorkingDir());
		JVMContainer container = new JVMContainer(workingDir);
		container.setArtifactPath(res.getArtifactFQN());
		container.setEntryPoint(res.getEntryPoint());
		
		// using default repositories:
		if (res.getRepositories() != null) {
			for (RepositoryDescriptor repo : res.getRepositories()){
				container.addRemoteRepository(RepositoryFactory.createRepository(repo));
			}
		}

		container.build();
		container.run();
		log.info("completed");

	}

}
