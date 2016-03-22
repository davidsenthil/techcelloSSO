package com.aspire.techcello.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.aspire.techcello.handlers.AuthHandler;
import com.aspire.techcello.handlers.AuthProvider;
import com.aspire.techcello.handlers.GoogleAuthProvider;
import com.aspire.techcello.handlers.LoginDisplayBean;
import com.aspire.techcello.handlers.TechcelloAuthProvider;

public class Controller extends HttpServlet {
	
	public static final String PARAM_AUTH_PROVIDER = "ParamAuthProvider";
	public static final String PARAM_OPENID_REDIRECT = "openIdRedirect";
	public static final String PARAM_ACTION = "ParamAction";
	
	private static final long serialVersionUID = 2994545097322870357L;
	private static final String JSP_LOGIN = "/jsp/login.jsp";
	private static final String JSP_LOGIN_RESULT = "/jsp/loginResult.jsp";
	private Map<String, AuthProvider> authProviders = new HashMap<String, AuthProvider>();
	final static Logger logger = Logger.getLogger(Controller.class);

	@Override
	public void init() throws ServletException {
		super.init();
		// Add the available Auth providers
		GoogleAuthProvider googleAuthProvider = new GoogleAuthProvider();
		authProviders.put(googleAuthProvider.getName(), googleAuthProvider);
		authProviders.put(TechcelloAuthProvider.PROVIDER_NAME, new TechcelloAuthProvider());
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String nextPage = JSP_LOGIN;
		LoginDisplayBean displayBean = new LoginDisplayBean();
		displayBean.setImagePath(getDocsPath(request));
		displayBean.setFullContextPath(request.getContextPath() + request.getServletPath());
		logger.debug("reqequest URI: " + request.getRequestURI());
	
		String action = request.getParameter(PARAM_ACTION);
		String authProviderStr = request.getParameter(PARAM_AUTH_PROVIDER);
		if (isCallbackRequest(request.getRequestURI())) {
			logger.info("Returning call from Auth Provider");
			action = AuthHandler.ACTION_AUTH_REDIRECT;
			authProviderStr = TechcelloAuthProvider.PROVIDER_NAME;
		}
		AuthProvider authProvider = authProviders.get(authProviderStr);
		AuthHandler authHandler = new AuthHandler(authProvider);
		logger.debug("Parameters: action: " + action + "provider: " + authProviderStr);

		if (AuthHandler.ACTION_OPEN_AUTH_PROVIDER_LOGIN.equals(action)) {
			// Redirect to Auth Provider's login page
			response.sendRedirect(authHandler.getLocationUri());
			return;
		
		} else if (AuthHandler.ACTION_AUTH_REDIRECT.equals(action)) {
			authHandler.processAuthResponse(request);
			displayBean.setAuthStatus(authHandler.getAuthStatus());
			displayBean.setAuthStatusMessage(authHandler.getAuthStatusMessage());
			displayBean.setTokenValues(authHandler.getTokenValueMessagesRows());
			displayBean.setUserFullName(authHandler.getUserFullName());
			nextPage = JSP_LOGIN_RESULT;
		}
			
		request.setAttribute("DisplayBean", displayBean);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);		
	}

	private boolean isCallbackRequest(String requestURI) {
		return requestURI != null && requestURI.endsWith("Controller/callback");
	}

	/**
	 * Document path for images, styles and etc.
	 */
	private String getDocsPath(HttpServletRequest request) {
		String docsPath = "http://" 
				+ request.getServerName() 
				+ ":" + request.getServerPort() 
				+ request.getServletContext().getContextPath()
				+ "/docs/";
		
		return docsPath;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	
}
