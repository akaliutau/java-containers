package org.containers.resolver;

import java.util.List;

import org.containers.model.ResolverResult;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.repository.RemoteRepository;

public interface Resolver {

	ResolverResult resolve(String coords, List<RemoteRepository> repositories);

	void deploy(Artifact artifact, Artifact pom, RemoteRepository nexus) throws DeploymentException;

}