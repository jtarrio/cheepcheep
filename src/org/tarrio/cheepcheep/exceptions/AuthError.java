package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class AuthError extends CheepCheepException {

	public AuthError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public AuthError(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public AuthError(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
