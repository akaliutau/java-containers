package org.containers.engine;

/**
 * 
 * Returns ASCII text logo: JC (Java Containers)
 * 
 * @author akaliutau
 *
 */
public class Logo {
	public final static String[] logoLines = { 
			"    ___  ________         ", 
			"   |\\  \\|\\   ____\\    ",
			"   \\ \\  \\ \\  \\___|    ", 
			" __ \\ \\  \\ \\  \\       ", 
			"|\\  \\\\_\\  \\ \\  \\____  ",
			"\\ \\________\\ \\_______\\", 
			" \\|________|\\|_______|", 
			"", 
			" Java Containers, v.1.0.0"

	};

	public final static String logo = String.join("\r\n", logoLines);

	public static void printLogo() {
		System.out.println(logo);
	}
}
