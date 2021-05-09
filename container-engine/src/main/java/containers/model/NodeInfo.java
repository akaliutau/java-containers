package containers.model;

public class NodeInfo {

	private final int count;

	private int index;

	public NodeInfo(int count) {
		this.count = count;
	}

	public String formatIndentation(boolean end) {
		boolean isLast = index + 1 >= count;
		if (end) {
			return isLast ? "\\- " : "+- ";
		}
		return isLast ? "   " : "|  ";
	}
	
	public void incIndex() {
		index ++;
	}

}
