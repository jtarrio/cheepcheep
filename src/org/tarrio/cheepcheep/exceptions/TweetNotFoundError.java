package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class TweetNotFoundError extends CheepCheepException {

	public TweetNotFoundError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public TweetNotFoundError(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public TweetNotFoundError(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
