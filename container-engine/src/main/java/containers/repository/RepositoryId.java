package containers.repository;

public enum RepositoryId {
	
	CENTRAL("central"),
	NEXUS("nexus"),
	LOCAL("local");
	
	private String name;
	
	private RepositoryId(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
