package com.xiao1zhao2.webflowcontrol.filter.flowcontrol;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FlowControlManage {

	private static FlowControlManage instance = new FlowControlManage();

	public static FlowControlManage getInstance() {
		return instance;
	}

	private static final int CONCURRENCY_LIMIT = 2;

	private static final Logger logger = Logger.getLogger(FlowControlManage.class);

	private ThreadLocal<FlowControlRequest> currentRequest = new ThreadLocal<FlowControlRequest>();

	private ConcurrentHashMap<String, Queue<FlowControlRequest>> flowControlMap = new ConcurrentHashMap<String, Queue<FlowControlRequest>>();

	private ConcurrentHashMap<Long, FlowControlRequest> threadRequestMap = new ConcurrentHashMap<Long, FlowControlRequest>();

	private FlowControlManage() {

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				for (String requestKey : flowControlMap.keySet()) {

					FlowControlRequest requestInQueue = null;
					FlowControlRequest requestInThread = null;
					Queue<FlowControlRequest> queue = flowControlMap.get(requestKey);

					while (queue != null && (requestInQueue = queue.peek()) != null) {
						long threadId = requestInQueue.getThreadId();
						requestInThread = threadRequestMap.get(threadId);
						if (!requestInQueue.equals(requestInThread)) {
							queue.poll();
							logger.warn("remove expired request " + requestInQueue.getRequestURL());
							continue;
						}
						break;
					}
				}

			}
		}, new Date(), 200);
	}

	public void filterBefore(HttpServletRequest request) {

		String requestKey = request.getRequestURI();
		long requestTime = System.currentTimeMillis();
		long threadId = Thread.currentThread().getId();
		FlowControlRequest flowControlRequest = new FlowControlRequest(requestKey, requestTime, threadId);

		if (flowControlMap.get(requestKey) == null) {
			flowControlMap.putIfAbsent(requestKey, new ConcurrentLinkedQueue<FlowControlRequest>());
		}

		Queue<FlowControlRequest> queue = flowControlMap.get(requestKey);

		if (queue == null || queue.size() >= CONCURRENCY_LIMIT || !queue.offer(flowControlRequest)) {
			throw new FlowControlException(flowControlRequest.getRequestURL());
		}

		currentRequest.set(flowControlRequest);
		threadRequestMap.put(threadId, flowControlRequest);
	}

	public void filterAfter(HttpServletRequest request) {

		String requestKey = request.getRequestURI();
		FlowControlRequest flowControlRequest = currentRequest.get();
		if (flowControlRequest != null) {
			Queue<FlowControlRequest> queue = flowControlMap.get(requestKey);
			long timeCost = System.currentTimeMillis() - flowControlRequest.getRequestTime();
			if (timeCost > 200) {
				logger.warn(flowControlRequest.getRequestURL() + " timeout " + timeCost + ", queue size is " + queue.size());
			}
			queue.remove(flowControlRequest);
			currentRequest.remove();
		}
	}

}
