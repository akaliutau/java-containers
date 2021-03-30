package org.containers.logger;

import java.io.PrintStream;

/**
 * A rudimentary system logger using only System.out
 * 
 * @author akaliutau
 *
 * @param <T>
 */
public class SystemLogger<T> implements ConsoleLogger<T> {

	@Override
	public void print(T in) {
		System.out.print(toString(in));

	}

	@Override
	public void println(T in) {
		System.out.println(toString(in));

	}

	private static String toString(Object o) {
		return o == null ? "null" : o.toString();
	}

	@Override
	public PrintStream getStream() {
		return System.out;
	}

}
