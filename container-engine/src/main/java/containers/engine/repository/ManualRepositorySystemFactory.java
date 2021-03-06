package containers.engine.repository;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for repository system instances that employs Maven Artifact
 * Resolver's built-in service locator to wire up the system's
 * components.
 * 
 * @author akaliutau
 *
 */
public class ManualRepositorySystemFactory {
	private static final Logger log = LoggerFactory.getLogger(ManualRepositorySystemFactory.class);

	public static RepositorySystem newRepositorySystem() {
		/*
		 * This is an Aether's components implement
		 * org.eclipse.aether.spi.locator.Service to ease manual wiring and using the
		 * prepopulated DefaultServiceLocator
		 * 
		 * We only need to register the repository
		 * connector and transporter factories.
		 */
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
			
			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				log.error("Service creation failed for {} with implementation {}", type, impl, exception);
			}
			
		});

		return locator.getService(RepositorySystem.class);
	}

}
