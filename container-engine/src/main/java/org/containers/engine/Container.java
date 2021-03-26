package org.containers.engine;

import java.io.File;
import java.nio.file.Path;

public class Container {
	

	private final Path workingDir;
	private SystemProcess process;

	public Container(Path workingDir) {
		this.workingDir = workingDir;
	}
	
	public void sync() {
		
	}

	public void setProcess(SystemProcess process) {
		this.process = process;
	}

}
