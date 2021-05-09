package containers.model;

import java.io.Serializable;

public class Credentials implements Serializable {
	private static final long serialVersionUID = 3922715196173764678L;

	private String username;
	private String password;

	public Credentials(String username, String password) {
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
