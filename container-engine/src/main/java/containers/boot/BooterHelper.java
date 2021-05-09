package containers.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import containers.engine.repository.ManualRepositorySystemFactory;
import containers.logger.ConsoleRepositoryListener;
import containers.logger.ConsoleTransferListener;
import containers.repository.RepositoryFactory;
import containers.repository.RepositoryId;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;

/**
 * A helper to boot the repository system and a repository system session.
 * Added mostly for demos
 * 
 * @author akaliutau
 */

public class BooterHelper {

	public static RepositorySystem newRepositorySystem() {
		return ManualRepositorySystemFactory.newRepositorySystem();
		// return
		// org.apache.maven.resolver.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
		// return
		// org.apache.maven.resolver.examples.sisu.SisuRepositorySystemFactory.newRepositorySystem();
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(LocalRepository localRepository, RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepository));

		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

	public static List<RemoteRepository> newRepositories() {
		return new ArrayList<>(Collections.singletonList(RepositoryFactory.createRepository(RepositoryId.CENTRAL)));
	}
	

	

}
