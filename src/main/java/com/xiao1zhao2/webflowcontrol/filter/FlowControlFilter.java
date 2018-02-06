package com.xiao1zhao2.webflowcontrol.filter;

import com.xiao1zhao2.webflowcontrol.filter.flowcontrol.FlowControlException;
import com.xiao1zhao2.webflowcontrol.filter.flowcontrol.FlowControlManage;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
		urlPatterns = {"/*"},
		asyncSupported = true,
		dispatcherTypes = {DispatcherType.REQUEST},
		initParams = {@WebInitParam(name = "encoding", value = "UTF-8")}
)
public class FlowControlFilter implements Filter {

	private Logger logger = Logger.getLogger(FlowControlFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("========== FlowControlFilter init ==========");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		try {
			FlowControlManage.getInstance().filterBefore(request);
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (FlowControlException f) {
			logger.warn("FlowControlException " + f.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			FlowControlManage.getInstance().filterAfter(request);
		}
	}

	@Override
	public void destroy() {
		logger.warn("========== FlowControlFilter destroy ==========");
	}

}
