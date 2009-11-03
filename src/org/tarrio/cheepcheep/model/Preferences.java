package org.tarrio.cheepcheep.model;

public class Preferences {

	private String username;
	private String consumerToken;
	private String consumerSecret;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConsumerToken() {
		return consumerToken;
	}

	public void setConsumerToken(String consumerToken) {
		this.consumerToken = consumerToken;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

}
