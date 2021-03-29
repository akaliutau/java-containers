package org.containers.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.aether.graph.DependencyNode;

/**
 */
public class ResolverResult {
	private DependencyNode root;
	private List<ArtifactWrapper> resolvedFiles;
	private String resolvedClassPath;
	private List<Exception> exceptions = new ArrayList<>();
	
	public ResolverResult() {
		
	}

	public ResolverResult(DependencyNode root, List<ArtifactWrapper> resolvedFiles, String resolvedClassPath) {
		this.root = root;
		this.resolvedFiles = resolvedFiles;
		this.resolvedClassPath = resolvedClassPath;
	}

	public DependencyNode getRoot() {
		return root;
	}

	public List<ArtifactWrapper> getResolvedFiles() {
		return resolvedFiles;
	}

	public String getResolvedClassPath() {
		return resolvedClassPath;
	}
	
	public boolean success() {
		return exceptions.isEmpty();
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}
	
	public String getFirstError() {
		return exceptions.isEmpty() ? "N/A" : exceptions.get(0).getMessage();
	}
	
	public void addException(Exception e){
		this.exceptions.add(e);
	}

	public void setRoot(DependencyNode root) {
		this.root = root;
	}

	public void setResolvedFiles(List<ArtifactWrapper> resolvedFiles) {
		this.resolvedFiles = resolvedFiles;
	}

	public void setResolvedClassPath(String resolvedClassPath) {
		this.resolvedClassPath = resolvedClassPath;
	}
}
