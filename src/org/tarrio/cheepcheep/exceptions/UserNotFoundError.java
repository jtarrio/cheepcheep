package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class UserNotFoundError extends CheepCheepException {

	public UserNotFoundError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public UserNotFoundError(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public UserNotFoundError(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
