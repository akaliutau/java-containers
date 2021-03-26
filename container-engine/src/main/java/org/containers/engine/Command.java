package org.containers.engine;

import java.util.ArrayList;
import java.util.List;

public class Command {

	private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	List<String> args = new ArrayList<>();

	public Command() {
		if (isWindows) {
			args.add("cmd.exe");
			args.add("/c");
		} else {
			args.add("sh");
			args.add("-c");
		}
	}

	public void addArg(String s) {
		args.add(s);
	}

	public List<String> params() {
		return args;
	}

	public static class Builder {
		private Command command;

		public Builder() {
			command = new Command();
		}
		
		public static Builder init() {
			return new Builder();
		}

		public Builder addArg(String arg) {
			command.addArg(arg);
			return this;
		}
		
		public Command build() {
			return command;
		}
	}
}
