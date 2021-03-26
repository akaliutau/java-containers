package org.containers.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void testHashes() {
		Path p1 = Path.of("/dir1/test1");
		Path p2 = Path.of("/dir1/test2");
		assertNotEquals(p1, p2);

		Path p1_1 = Path.of("/dir1/test1");
		Path p2_1 = Path.of("/dir1/test1");
		assertEquals(p1_1, p2_1);

		Path p1_2 = Path.of("/dir2/test1");
		Path p2_2 = Path.of("/dir1/test1");
		assertNotEquals(p1_2, p2_2);

		String p1_3 = FileUtils.name(Path.of("/dir2/test1"));
		String p2_3 = FileUtils.name(Path.of("/dir1/test1"));
		assertEquals(p1_3, p2_3);

	}

	@Test
	public void testNameStrip() {
		Path etalon = Path.of("/dir1/test1.jar");
		Path p1 = Path.of("/dir1/test1.jar.sha1");
		assertEquals(etalon, FileUtils.stripMame(p1));

		Path p2 = Path.of("/dir1/test1.jar.sha");
		assertNotEquals(etalon, FileUtils.stripMame(p2));
	}

	@Test
	public void testNameExtraction() throws URISyntaxException, IOException {
		Path p1 = Path.of(getClass().getResource("/samples/jar1.sha1").toURI());
		Path p2 = Path.of(getClass().getResource("/samples/jar1a.sha1").toURI());
		Path p3 = Path.of(getClass().getResource("/samples/jar2.sha1").toURI());

		assertTrue(FileUtils.areFilesIdenticalMemoryMapped(p1, p2));
		assertFalse(FileUtils.areFilesIdenticalMemoryMapped(p1, p3));
	}

	@Test
	public void testFileCopying() {

	}

}
