package org.containers.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamRunner implements Runnable {
	private InputStream inputStream;
	private Consumer<String> consumer;
	private int counter = 0;

	public StreamRunner(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Stream<String> lines = reader.lines()) {
            lines.forEach(consumer);
            System.out.println("line " + counter  ++);
        } catch (IOException e) {
			e.printStackTrace();
		} 
        System.out.println("exit thread" + Thread.currentThread().getName());
	}
}
