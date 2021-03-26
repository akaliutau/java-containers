package org.containers.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.containers.engine.repository.ManualRepositorySystemFactory;
import org.containers.logger.ConsoleRepositoryListener;
import org.containers.logger.ConsoleTransferListener;

/**
 * A helper to boot the repository system and a repository system session.
 * Added mostly for demos
 * 
 * @author akaliutau
 */

public class Booter {

	public static RepositorySystem newRepositorySystem() {
		return ManualRepositorySystemFactory.newRepositorySystem();
		// return
		// org.apache.maven.resolver.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
		// return
		// org.apache.maven.resolver.examples.sisu.SisuRepositorySystemFactory.newRepositorySystem();
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository("target/local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

	public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session) {
		return new ArrayList<>(Collections.singletonList(newCentralRepository()));
	}

	private static RemoteRepository newCentralRepository() {
		return new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build();
	}

}
