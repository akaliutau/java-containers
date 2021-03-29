package org.containers.demo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.containers.engine.Logo;
import org.containers.model.ResolverResult;
import org.containers.resolver.DefaultResolver;

/**
 * Collects the transitive dependencies of an artifact.
 */
public class GetDependencyTree {

	/**
	 * Main.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("------------------------------------------------------------");
		System.out.println(GetDependencyTree.class.getSimpleName());
		Path targetDirectory = Paths.get("target");
		
		DefaultResolver resolver = new DefaultResolver("http://localhost:8081/nexus/content/groups/public", targetDirectory + File.separator + "local-repo");

		ResolverResult res = resolver.resolve("org.apache.maven:maven-resolver-provider:3.6.1");


		System.out.println("arts" + res.getResolvedFiles());
		System.out.println("arts" + res.getResolvedClassPath());
	}
	


}
