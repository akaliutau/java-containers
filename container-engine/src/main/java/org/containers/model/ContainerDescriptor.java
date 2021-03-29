package org.containers.model;

import java.io.Serializable;
import java.util.List;

public class ContainerDescriptor implements Serializable {
	private static final long serialVersionUID = -8592308168402268289L;
	
	private String workingDir;
	private String artifactFQN;
	private String entryPoint;
	private List<RepositoryDescriptor> repositories;
	

	public ContainerDescriptor() {
	}

	public String getWorkingDir() {
		return workingDir;
	}
	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
	public String getArtifactFQN() {
		return artifactFQN;
	}
	public void setArtifactFQN(String artifactFQN) {
		this.artifactFQN = artifactFQN;
	}
	public String getEntryPoint() {
		return entryPoint;
	}
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}

	public List<RepositoryDescriptor> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<RepositoryDescriptor> repositories) {
		this.repositories = repositories;
	}

	
}
