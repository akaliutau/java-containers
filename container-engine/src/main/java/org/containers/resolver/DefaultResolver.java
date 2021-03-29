package org.containers.resolver;

import org.containers.boot.Booter;
import org.containers.engine.graph.ConsoleDependencyGraphDumper;
import org.containers.model.Credentials;
import org.containers.model.ResolverResult;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

public class DefaultResolver implements Resolver {
	
	private final String remoteRepository;// http://localhost:8081/nexus/content/groups/public

	private final RepositorySystem repositorySystem;

	private final LocalRepository localRepository;
	
	private Credentials creds;
	
	private boolean verbose = true;

	public DefaultResolver(String remoteRepository, String localRepository) {
		this.remoteRepository = remoteRepository;
		this.repositorySystem = Booter.newRepositorySystem();
		this.localRepository = new LocalRepository(localRepository);
	}
	
	public DefaultResolver(String remoteRepository, String localRepository, Credentials creds) {
		this(remoteRepository, localRepository);
		this.creds = creds;
	}

	/**
	 * Builds a new session using LocalRepository as a cache
	 * NOTE: Aether uses local repository by default, so simply set to not use remote if this is not required (flag UPDATE_POLICY_NEVER)
	 * 
	 * @return RepositorySystemSession - ready to use session
	 */
	private RepositorySystemSession newSession() {
		DefaultRepositorySystemSession session = Booter.newRepositorySystemSession(repositorySystem);
		session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));
		session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER);
		return session;
	}

	@Override
	public ResolverResult resolve(String coords) {
		
		Artifact artifact = new DefaultArtifact(coords);
		RepositorySystemSession session = newSession();
		

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(new Dependency(artifact, ""));
		collectRequest.setRepositories(Booter.newRepositoriesWithLocal(repositorySystem, session));

		ConsoleDependencyGraphDumper dump = new ConsoleDependencyGraphDumper();
		
		DependencyRequest dependencyRequest = new DependencyRequest();
		dependencyRequest.setCollectRequest(collectRequest);

		DependencyNode rootNode = null;
		ResolverResult result = new ResolverResult();
		try {
			rootNode = repositorySystem.resolveDependencies(session, dependencyRequest).getRoot();
			rootNode.accept(dump);

			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			rootNode.accept(nlg);
		} catch (DependencyResolutionException e) {
			result.addException(e);
		}
		result.setRoot(rootNode);
		result.setResolvedFiles(dump.getDependencies());
		result.setResolvedClassPath(dump.getClassPath());
		return result;
		
	}
	
	public void deploy(Artifact artifact, Artifact pom) throws DeploymentException {
		RepositorySystemSession session = newSession();

		
		RemoteRepository nexus = new RemoteRepository.Builder("nexus", "default", remoteRepository)
				.setAuthentication(getAuthentication()).build();

		DeployRequest deployRequest = new DeployRequest();
		deployRequest.addArtifact(artifact).addArtifact(pom);
		deployRequest.setRepository(nexus);

		repositorySystem.deploy(session, deployRequest);
	}
	
	private Authentication getAuthentication() {
		return creds == null ? null : new AuthenticationBuilder().addUsername(creds.getUsername()).addPassword(creds.getPassword()).build();
	}


	public String getRemoteRepository() {
		return remoteRepository;
	}

	
}
