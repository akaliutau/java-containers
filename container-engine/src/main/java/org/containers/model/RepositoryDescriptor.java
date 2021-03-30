package org.containers.model;

import java.io.Serializable;

import org.containers.repository.RepositoryId;

public class RepositoryDescriptor implements Serializable {

	private static final long serialVersionUID = 5904417659705361402L;

	private RepositoryId repositoryId;
	private String url;
	private Credentials auth;
	

	public RepositoryDescriptor() {
	}


	public RepositoryId getRepositoryId() {
		return repositoryId;
	}


	public void setRepositoryId(RepositoryId repositoryId) {
		this.repositoryId = repositoryId;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public Credentials getAuth() {
		return auth;
	}


	public void setAuth(Credentials auth) {
		this.auth = auth;
	}



	
}
