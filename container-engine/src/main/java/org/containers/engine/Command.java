package org.containers.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Command pattern
 *  
 * @author akaliutau
 *
 */
public class Command {

	private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	List<String> args = new ArrayList<>();

	public Command() {
	}
	
	public void setConsole() {
		if (isWindows) {
			args.add("cmd.exe");
			args.add("/c");
		} else {
			args.add("sh");
			args.add("-c");
		}
	}

	public void addArg(String arg) {
		args.add(arg);
	}

	public void addArgs(List<String> args) {
		args.addAll(args);
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
			if (!isEmpty(arg))
				command.addArg(arg);
			return this;
		}

		public Builder addArg(List<String> args) {
			if (args != null && !args.isEmpty())
				command.addArgs(args);
			return this;
		}

		public Command build() {
			return command;
		}
	}
	
	private static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
}
