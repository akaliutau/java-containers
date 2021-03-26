package org.containers.model;

public class Credentials {

	private char[] username;
	
	private char[] password;

	public Credentials(char[] username, char[] password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return String.valueOf(username);
	}

	public String getPassword() {
		return String.valueOf(password);
	}

}
