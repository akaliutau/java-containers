package org.containers.logger;

import java.io.PrintStream;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * A simple repository listener that logs events to the console.
 * 
 * @author akaliutau
 *
 */
public class ConsoleRepositoryListener extends AbstractRepositoryListener {

	private PrintStream out;

	public ConsoleRepositoryListener() {
		this(null);
	}

	public ConsoleRepositoryListener(PrintStream out) {
		this.out = (out != null) ? out : System.out;
	}

	@Override
	public void artifactDeployed(RepositoryEvent event) {
		out.println("Deployed " + event.getArtifact() + " to " + event.getRepository());
	}

	@Override
	public void artifactDeploying(RepositoryEvent event) {
		out.println("Deploying " + event.getArtifact() + " to " + event.getRepository());
	}

	@Override
	public void artifactDescriptorInvalid(RepositoryEvent event) {
		out.println("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
	}

	@Override
	public void artifactDescriptorMissing(RepositoryEvent event) {
		out.println("Missing artifact descriptor for " + event.getArtifact());
	}

	@Override
	public void artifactInstalled(RepositoryEvent event) {
		out.println("Installed " + event.getArtifact() + " to " + event.getFile());
	}

	@Override
	public void artifactInstalling(RepositoryEvent event) {
		out.println("Installing " + event.getArtifact() + " to " + event.getFile());
	}

	@Override
	public void artifactResolved(RepositoryEvent event) {
		out.println("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	@Override
	public void artifactDownloading(RepositoryEvent event) {
		out.println("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	@Override
	public void artifactDownloaded(RepositoryEvent event) {
		out.println("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	@Override
	public void artifactResolving(RepositoryEvent event) {
		out.println("Resolving artifact " + event.getArtifact());
	}

	@Override
	public void metadataDeployed(RepositoryEvent event) {
		out.println("Deployed " + event.getMetadata() + " to " + event.getRepository());
	}

	@Override
	public void metadataDeploying(RepositoryEvent event) {
		out.println("Deploying " + event.getMetadata() + " to " + event.getRepository());
	}

	@Override
	public void metadataInstalled(RepositoryEvent event) {
		out.println("Installed " + event.getMetadata() + " to " + event.getFile());
	}

	@Override
	public void metadataInstalling(RepositoryEvent event) {
		out.println("Installing " + event.getMetadata() + " to " + event.getFile());
	}

	@Override
	public void metadataInvalid(RepositoryEvent event) {
		out.println("Invalid metadata " + event.getMetadata());
	}

	@Override
	public void metadataResolved(RepositoryEvent event) {
		out.println("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
	}

	@Override
	public void metadataResolving(RepositoryEvent event) {
		out.println("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
	}



}
