package com.xiao1zhao2.webflowcontrol.filter.flowcontrol;

public class FlowControlException extends RuntimeException {

	public FlowControlException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
