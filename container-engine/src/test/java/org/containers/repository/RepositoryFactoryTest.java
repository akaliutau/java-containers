package org.containers.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import containers.repository.RepositoryFactory;
import containers.repository.RepositoryId;

public class RepositoryFactoryTest {

	@Test
	public void testGetDefaults() {
		RemoteRepository repo = RepositoryFactory.createRepository(RepositoryId.CENTRAL);
		assertNotNull(repo);
	}

	@Test
	public void testNonExistingDefaultRepo() {
		boolean caught = false;
		try {
			RemoteRepository repo = RepositoryFactory.createRepository(RepositoryId.NEXUS);
		} catch (Exception e) {
			caught = true;
		}
		assertTrue(caught);
	}
}
