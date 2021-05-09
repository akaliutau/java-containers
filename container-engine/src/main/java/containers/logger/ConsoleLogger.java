package containers.logger;

import java.io.PrintStream;

/**
 * Base generic interface for logger
 * 
 * @author akaliutau
 *
 * @param <T>
 */
public interface ConsoleLogger<T> {
	void print(T in);
	void println(T in);
	PrintStream getStream();
}
