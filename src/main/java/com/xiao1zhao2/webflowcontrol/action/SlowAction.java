package com.xiao1zhao2.webflowcontrol.action;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/slow")
public class SlowAction extends HttpServlet {

	private Logger logger = Logger.getLogger(SlowAction.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Thread.sleep(1000);
			resp.getWriter().print("this is a slow response");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
