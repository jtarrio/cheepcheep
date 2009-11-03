package org.tarrio.cheepcheep.model;

import java.util.Date;

public class Tweet {

	private Long id;
	private Date dateTime;
	private String screenName;
	private String text;
	private Long inReplyToId;
	private String inReplyToScreenName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setInReplyToId(Long inReplyToId) {
		this.inReplyToId = inReplyToId;
	}

	public Long getInReplyToId() {
		return inReplyToId;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

}
