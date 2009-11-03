package org.tarrio.cheepcheep.exceptions;

@SuppressWarnings("serial")
public class NetError extends CheepCheepException {

	public NetError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NetError(String detailMessage) {
		super(detailMessage);
	}

	public NetError(Throwable throwable) {
		super(throwable);
	}

}
