package containers.model;

import java.io.File;

import org.eclipse.aether.artifact.Artifact;

public class ArtifactWrapper {

	private final Artifact artifact;
	

	public ArtifactWrapper(Artifact a) {
		this.artifact = a;
	}
	
	public Artifact getArtifact() {
		return artifact;
	}

	public String getGroupId() {
		return artifact.getGroupId();
	}

	public String getArtefactId() {
		return artifact.getArtifactId();
	}

	public String getVersion() {
		return artifact.getVersion();
	}

	public String getClassifier() {
		return artifact.getClassifier();
	}

	public File getFile() {
		return artifact.getFile();
	}

	public boolean exists() {
		return artifact.getFile() != null && artifact.getFile().exists();
	}

	public String getExtension() {
		return artifact.getExtension();
	}


	@Override
	public String toString() {
		return "Artifact [groupId=" + getGroupId() + ", artefactId=" + getArtefactId() + ", version=" + getVersion()
				+ ", extension=" + getExtension() + ", classifier=" + getClassifier() + ", file=" + artifact.getFile() + "]";
	}

}
