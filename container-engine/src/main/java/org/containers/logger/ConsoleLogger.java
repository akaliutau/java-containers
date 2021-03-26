package org.containers.logger;

import java.io.PrintStream;

public interface ConsoleLogger<T> {
	void print(T in);
	void println(T in);
	PrintStream getStream();
}
