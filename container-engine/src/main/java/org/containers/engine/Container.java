package org.containers.engine;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.containers.exception.ContainerException;
import org.containers.model.ResolverResult;
import org.containers.resolver.DefaultResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Container {
	private static final Logger log = LoggerFactory.getLogger(Container.class);
	
	private final String cid = UUID.randomUUID().toString();
	private final Path workingDir;
	private SystemProcess process;
	
	private String jarPath;
	private String className;
	private List<String> args = new ArrayList<>();
	
	private String classPath;

	public Container(Path workingDir) {
		this.workingDir = workingDir;
		Logo.printLogo();
	}
	
	public void build() {
		checkIntegrity();
		String dir = workingDir + File.separator + "local-repo";
		log.info("building container {} in {}", cid, dir);
		DefaultResolver resolver = new DefaultResolver("http://localhost:8081/nexus/content/groups/public", dir);
		ResolverResult res = resolver.resolve(jarPath);
		classPath = res.getResolvedClassPath();
		if (!res.success()) {
			throw new ContainerException("failed to build container, first error " + res.getFirstError());
		}
	}
	
	public void setEntryPoint(String jarPath, String className) {
		this.jarPath = jarPath;
		this.className = className;
		log.info("entry point set to {}, class {}", jarPath, className);
	}
	
	public void addArgument(String arg) {
		args.add(arg);
	}
	
	public void run() {
		Command cmd = Command.Builder.init()
				.addArg("java")
				.addArg("-cp")
				.addArg(classPath)
				.addArg(className)
				.build();
		log.info("command line: {}", cmd.params());
		SystemProcess proc = new SystemProcess(workingDir, cmd);
		proc.exec();
		proc.printState();
	}

	public void setProcess(SystemProcess process) {
		this.process = process;
	}
	
	public void checkIntegrity() {
		if (jarPath == null) {
			throw new ContainerException("set jarPath before building container");
		}
	}

}
