package org.containers.model;

import java.io.Serializable;
import java.util.List;

/**
 * A POJO class describing essential properties of Container
 * @author akaliutau
 *
 */
public class ContainerDescriptor implements Serializable {
	private static final long serialVersionUID = -8592308168402268289L;
	
	private String workingDir;
	private String artifactFQN;
	private String entryPoint;
	private List<RepositoryDescriptor> repositories;
	private List<String> dropArtifacts;
	

	public ContainerDescriptor() {
	}

	public String getWorkingDir() {
		return workingDir;// null if current directory is used
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

	public List<String> getDropArtifacts() {
		return dropArtifacts;
	}

	public void setDropArtifacts(List<String> dropArtifacts) {
		this.dropArtifacts = dropArtifacts;
	}

	
}
