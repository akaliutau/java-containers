package org.containers.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.containers.model.Credentials;
import org.containers.model.RepositoryDescriptor;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

/**
 * Contains pre-defined descriptors for RemoteRepository
 * 
 * examples of URL for different types of repository:
 * 
 * local (m2):
 * 
 * file:/home/user/.m2/repository
 * 
 * local Nexus:
 * 
 * http://localhost:8081/nexus/content/groups/public
 * 
 * 
 * @author akaliutau
 *
 */
public class RepositoryFactory {
	
	private static final String RepositoryType = "default";
	
	private static final Map<RepositoryId, Supplier<RemoteRepository>> map = new HashMap<>();
	
	static {
		map.put(RepositoryId.CENTRAL, central());
	}
	
	public static RemoteRepository createRepository(RepositoryId id) {
		if (!map.containsKey(id)) {
			throw new IllegalArgumentException("Cannot find predefined Repository with id = " + id);
		}
		return map.get(id).get();
	}
	
	public static RemoteRepository createRepository(RepositoryId id, String url) {
		return new RemoteRepository.Builder(id.toString(), RepositoryType, url).build();
	}
	
	public static RemoteRepository createRepositoryAuth(RepositoryId id, String url, Credentials creds) {
		return new RemoteRepository.Builder(id.toString(), RepositoryType, url).setAuthentication(getAuthentication(creds)).build();
	}
	
	public static RemoteRepository createRepository(RepositoryDescriptor descr) {
		if (descr.getAuth() == null) {
			return createRepository(descr.getRepositoryId(), descr.getUrl());
		}
		return createRepositoryAuth(descr.getRepositoryId(), descr.getUrl(), descr.getAuth());
	}

	
	private static Supplier<RemoteRepository> central(){
		return () -> {
			 return new RemoteRepository.Builder("central", RepositoryType, "https://repo.maven.apache.org/maven2/").build();
		};
	}
	
	private static Authentication getAuthentication(Credentials creds) {
		return creds == null ? null : new AuthenticationBuilder().addUsername(creds.getUsername()).addPassword(creds.getPassword()).build();
	}

}
