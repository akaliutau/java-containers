package org.containers.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import containers.model.ContainerDescriptor;
import containers.model.RepositoryDescriptor;
import containers.repository.RepositoryId;
import containers.util.JsonUtils;

public class JsonUtilsTest {

	private static final Logger log = LoggerFactory.getLogger(JsonUtilsTest.class);

	@Test
	public void testSerialization() {
		ContainerDescriptor cd = new ContainerDescriptor();
		cd.setWorkingDir("d:\\repos-research\\java-containers\\");
		cd.setArtifactFQN("org.containers:container-samples:1.0.0");
		cd.setEntryPoint("org.containers.demo.SimpleApplication");

		Gson g = new Gson();
		log.info(g.toJson(cd));
	}
	
	@Test
	public void testDeserialization() throws IOException {
		ContainerDescriptor cd = new ContainerDescriptor();
		cd.setWorkingDir("d:\\repos-research\\java-containers\\");
		cd.setArtifactFQN("org.containers:container-samples:1.0.0");
		cd.setEntryPoint("org.containers.demo.SimpleApplication");
		
		RepositoryDescriptor local = new RepositoryDescriptor();
		local.setRepositoryId(RepositoryId.LOCAL);
		local.setUrl("file:/home/user/.m2/repository");

		RepositoryDescriptor central = new RepositoryDescriptor();
		central.setRepositoryId(RepositoryId.CENTRAL);
		central.setUrl("https://repo.maven.apache.org/maven2/");
		cd.setRepositories(Arrays.asList(local, central));

		Gson g = new Gson();
		log.info(g.toJson(cd));
		File file = writeToFile(g.toJson(cd));
		ContainerDescriptor res = JsonUtils.fromJson(file.toPath(), ContainerDescriptor.class);
		assertNotNull(res);
		assertEquals(cd.getArtifactFQN(), res.getArtifactFQN());
	}


	public static File writeToFile(String cont) throws IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempFile = File.createTempFile("test", ".tmp", tempDir);
		FileWriter fileWriter = new FileWriter(tempFile, true);
		log.info("writing to file {}", tempFile.getAbsolutePath());
		try (BufferedWriter bw = new BufferedWriter(fileWriter)) {
			bw.write(cont);
		}
		return tempFile;
	}
}
