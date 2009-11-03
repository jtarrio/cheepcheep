package org.tarrio.cheepcheep.model;

public class User {

	private String screenName;
	private String realName;
	private String avatarUrl;
	private String description;
	private String location;
	private String homepage;
	private long numFollowing;
	private long numFollowers;
	private long numUpdates;
	private boolean secret;
	private boolean following;
	
	public String getScreenName() {
		return screenName;
	}
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getAvatarUrl() {
		return avatarUrl;
	}
	
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public long getNumFollowing() {
		return numFollowing;
	}
	
	public void setNumFollowing(long numFollowing) {
		this.numFollowing = numFollowing;
	}
	
	public long getNumFollowers() {
		return numFollowers;
	}
	
	public void setNumFollowers(long numFollowers) {
		this.numFollowers = numFollowers;
	}

	public long getNumUpdates() {
		return numUpdates;
	}
	
	public void setNumUpdates(long numUpdates) {
		this.numUpdates = numUpdates;
	}
	
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}
	
}
