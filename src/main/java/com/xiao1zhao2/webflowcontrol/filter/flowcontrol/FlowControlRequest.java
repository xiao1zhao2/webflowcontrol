package com.xiao1zhao2.webflowcontrol.filter.flowcontrol;

public class FlowControlRequest {

	private String requestURL;
	private long requestTime;
	private long threadId;

	public FlowControlRequest() {
	}

	public FlowControlRequest(String requestURL, long requestTime, long threadId) {
		this.requestURL = requestURL;
		this.requestTime = requestTime;
		this.threadId = threadId;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FlowControlRequest that = (FlowControlRequest) o;
		return requestTime == that.requestTime && threadId == that.threadId && requestURL.equals(that.requestURL);
	}

	@Override
	public int hashCode() {
		int result = requestURL.hashCode();
		result = 31 * result + (int) (requestTime ^ (requestTime >>> 32));
		result = 31 * result + (int) (threadId ^ (threadId >>> 32));
		return result;
	}

}
