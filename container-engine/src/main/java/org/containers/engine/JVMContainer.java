package org.containers.engine;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.containers.exception.ContainerException;
import org.containers.model.ResolverResult;
import org.containers.resolver.DefaultResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of container for java applications
 * @author akaliutau
 *
 */
public class JVMContainer implements Container {
	private static final Logger log = LoggerFactory.getLogger(JVMContainer.class);
	
	private final String cid = UUID.randomUUID().toString();
	private final Path workingDir;
	private SystemProcess process;
	private List<RemoteRepository> repositories;
	
	private String jarPath;
	private String className;
	private List<String> args = new ArrayList<>();
	
	private String classPath;

	public JVMContainer(Path workingDir) {
		this.workingDir = workingDir;
		this.repositories = new ArrayList<>();
		Logo.printLogo();
	}
	
	@Override
	public void build() {
		checkIntegrity();
		String dir = workingDir + File.separator + "local-repo";
		log.info("building container {} in {}", cid, dir);
		DefaultResolver resolver = new DefaultResolver(dir);
		ResolverResult res = resolver.resolve(jarPath, repositories);
		classPath = res.getResolvedClassPath();
		if (!res.success()) {
			throw new ContainerException("failed to build container, first error " + res.getFirstError());
		}
	}
	
	public void setArtifactPath(String jarPath) {
		this.jarPath = jarPath;
	}
	
	public void setEntryPoint(String className) {
		this.className = className;
		log.info("entry point set to class {}", className);
	}
	
	public void addArgument(String... args) {
		for (int i = 0; i < args.length; i++) {
			this.args.add(args[i]);
		}
	}
	
	public void addRemoteRepository(RemoteRepository... repos) {
		for (int i = 0; i < repos.length; i++) {
			this.repositories.add(repos[i]);
		}
	}
	
	public void addRemoteRepository(List<RemoteRepository> repos) {
		this.repositories.addAll(repos);
	}

	@Override
	public void run() {
		Command cmd = Command.Builder.init()
				.addArg("java")
				.addArg("-cp")
				.addArg(classPath)
				.addArg(className)
				.build();
		log.info("command line: {}", cmd.params());
		SystemProcess proc = new SystemProcess(workingDir, cmd);
		log.info("container {} started", cid);
		proc.exec();
		log.info("container {} finished", cid);
		proc.printState();
	}

	public void setProcess(SystemProcess process) {
		this.process = process;
	}
	
	public void checkIntegrity() {
		if (jarPath == null) {
			throw new ContainerException("set jar path before building container");
		}
		if (repositories.isEmpty()) {
			throw new ContainerException("set at least one repository (remote or local)");
		}

	}

}
