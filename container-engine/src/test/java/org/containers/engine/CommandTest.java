package org.containers.engine;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import containers.engine.Command;

public class CommandTest {

	private static final Logger log = LoggerFactory.getLogger(CommandTest.class);

	@Test
	public void functionalTest() {
		Command cmd = new Command();
		log.debug("cmd1: {}", cmd.params());
	}
	
}
