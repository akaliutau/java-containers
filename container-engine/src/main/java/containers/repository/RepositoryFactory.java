package containers.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import containers.model.Credentials;
import containers.model.RepositoryDescriptor;
import containers.util.FileUtils;

/**
 * Contains pre-defined descriptors for RemoteRepository
 * 
 * examples of URL for different types of repository:
 * 
 * Local maven repository(AKA .m2 repository):
 * 
 * file:/home/user/.m2/repository
 * 
 * Local Nexus:
 * 
 * http://localhost:8081/nexus/content/groups/public
 * 
 * 
 * @author akaliutau
 *
 */
public class RepositoryFactory {
	
	private static final String RepositoryType = "default";
	
	private static final Map<RepositoryId, Supplier<RemoteRepository>> preDefined = new HashMap<>();
	
	static {
		preDefined.put(RepositoryId.CENTRAL, central());
	}
	
	private static final Map<RepositoryId, Function<String, RemoteRepository>> preDefinedGenerators = new HashMap<>();
	
	static {
		preDefinedGenerators.put(RepositoryId.CENTRAL, centralGenerator());
		preDefinedGenerators.put(RepositoryId.LOCAL, localGenerator());
		preDefinedGenerators.put(RepositoryId.NEXUS, nexusGenerator());
	}

	
	public static RemoteRepository createRepository(RepositoryId id) {
		if (!preDefined.containsKey(id)) {
			throw new IllegalArgumentException("Cannot find predefined Repository with id = " + id);
		}
		return preDefined.get(id).get();
	}
	
	public static RemoteRepository createRepository(RepositoryId id, String url) {
		if (!preDefinedGenerators.containsKey(id)) {
			throw new IllegalArgumentException("Cannot find predefined Repository with id = " + id);
		}
		return preDefinedGenerators.get(id).apply(url);
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

	/**
	 * Concrete implementations of generators
	 * @return
	 */
	private static Supplier<RemoteRepository> central(){
		return () -> {
			 return new RemoteRepository.Builder("central", RepositoryType, "https://repo.maven.apache.org/maven2/").build();
		};
	}
	
	private static Function<String, RemoteRepository> centralGenerator(){
		return url -> {
			 return new RemoteRepository.Builder("central", RepositoryType, url).build();
		};
	}

	
	private static Function<String, RemoteRepository> localGenerator(){
		return url -> {
			 String uri = url == null ? FileUtils.getM2Directory() : url;
			 return new RemoteRepository.Builder("local", RepositoryType, uri).build();
		};
	}
	
	private static Function<String, RemoteRepository> nexusGenerator(){
		return url -> {
			 return new RemoteRepository.Builder("nexus", RepositoryType, url).build();
		};
	}


	
	private static Authentication getAuthentication(Credentials creds) {
		return creds == null ? null : new AuthenticationBuilder().addUsername(creds.getUsername()).addPassword(creds.getPassword()).build();
	}

}
