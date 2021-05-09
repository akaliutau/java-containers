package containers.observability;

import java.util.Date;

public class CheckPoint {
	long start = new Date().getTime();
	
	public long getTime() {
		return new Date().getTime() - start;
	}
	
	public String end() {
		return String.format("processed in %.4f sec", (float) getTime() / 1000);
	}
}
