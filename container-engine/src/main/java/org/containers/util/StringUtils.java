package org.containers.util;

import java.util.Arrays;

public class StringUtils {
	
	public static final char[] emptyBlock = new char[40];
	public static String block = null;
	
	static {
		Arrays.fill(emptyBlock, ' ');
		block = new String(emptyBlock);
	}
	
	public static String getStatus(long complete, long total) {
		if (total >= 1024) {
			return toKB(complete) + "/" + toKB(total) + " KB ";
		} else if (total >= 0) {
			return complete + "/" + total + " B ";
		} else if (complete >= 1024) {
			return toKB(complete) + " KB ";
		} else {
			return complete + " B ";
		}
	}

	public static void pad(StringBuilder buffer, int spaces) {
		while (spaces > 0) {
			int n = Math.min(spaces, block.length());
			buffer.append(block, 0, n);
			spaces -= n;
		}
	}


	public static long toKB(long bytes) {
		return (bytes + 1023) / 1024;
	}

}
