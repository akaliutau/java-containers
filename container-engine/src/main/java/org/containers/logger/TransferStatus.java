package org.containers.logger;

public enum TransferStatus {
	
	UPLOAD_INPROGRESS("Uploading"),
	DOWNLOAD_INPROGRESS("Downloading"),
	UPLOAD_COMPLETE("Uploaded"),
	DOWNLOAD_COMPLETE("Downloaded");
	
	private String name;

	private TransferStatus(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
