package org.containers.resolver;

import java.util.List;

import org.containers.boot.BooterHelper;
import org.containers.engine.graph.ConsoleDependencyGraphDumper;
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
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

/**
 * Default resolver for maven repositories
 * Mostly built on the basis of Eclipse Aether project
 * @author akaliutau
 *
 */
public class DefaultResolver implements Resolver {
	
	private final RepositorySystem repositorySystem;

	private final LocalRepository localRepository;
	
	private List<String> exclusions;
	
	private boolean verbose = true;

	public DefaultResolver(String localRepository) {
		this.repositorySystem = BooterHelper.newRepositorySystem();
		this.localRepository = new LocalRepository(localRepository);
	}
	

	/**
	 * Builds a new session using LocalRepository as a cache
	 * NOTE: Aether uses local repository by default, so simply set to not use remote if this is not required (flag UPDATE_POLICY_NEVER)
	 * 
	 * @return RepositorySystemSession - ready to use session
	 */
	private RepositorySystemSession newSession() {
		DefaultRepositorySystemSession session = BooterHelper.newRepositorySystemSession(localRepository, repositorySystem);
		session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));
		session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER);
		return session;
	}

	@Override
	public ResolverResult resolve(String coords, List<RemoteRepository> repositories) {
		
		Artifact artifact = new DefaultArtifact(coords);
		RepositorySystemSession session = newSession();
		

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(new Dependency(artifact, ""));
		collectRequest.setRepositories(repositories);

		ConsoleDependencyGraphDumper dump = new ConsoleDependencyGraphDumper();
		// in this demo it's too tricky to exclude containers engine's classes due to common parent
		dump.addExclusions(exclusions);
		
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
	
	@Override
	public void deploy(Artifact artifact, Artifact pom, RemoteRepository nexus) throws DeploymentException {
		RepositorySystemSession session = newSession();

		DeployRequest deployRequest = new DeployRequest();
		deployRequest.addArtifact(artifact).addArtifact(pom);
		deployRequest.setRepository(nexus);

		repositorySystem.deploy(session, deployRequest);
	}

	public void setExclusions(List<String> exclusions) {
		this.exclusions = exclusions;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	

	
}
