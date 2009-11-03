package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class ParseError extends CheepCheepException {

	public ParseError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ParseError(String detailMessage) {
		super(detailMessage);
	}

	public ParseError(Throwable throwable) {
		super(throwable);
	}

}
