package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class CheepCheepException extends Exception {

	public CheepCheepException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CheepCheepException(String detailMessage) {
		super(detailMessage);
	}

	public CheepCheepException(Throwable throwable) {
		super(throwable);
	}

}
