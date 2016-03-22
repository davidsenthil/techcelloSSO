package com.aspire.techcello.handlers;

import java.util.List;

import org.apache.oltu.oauth2.common.OAuthProviderType;

import com.aspire.techcello.controller.Controller;
import com.aspire.techcello.handlers.AuthHandler.Status;

public class LoginDisplayBean {
	
	private String fullContextPath;
	private String imagePath;
	private Status authStatus;
	private String authStatusMessage;
	private List<String> tokenValueMessagesRows;
	private String userFullName;
	
	public String getGoogleLoginActionURL() {
		return fullContextPath + "?" 
				+ Controller.PARAM_ACTION + "=" + AuthHandler.ACTION_OPEN_AUTH_PROVIDER_LOGIN
				+ "&"
				+ Controller.PARAM_AUTH_PROVIDER + "=" + OAuthProviderType.GOOGLE.toString();
	}
	
	public String getTechcelloLoginActionURL() {
		return fullContextPath + "?" 
				+ Controller.PARAM_ACTION + "=" + AuthHandler.ACTION_OPEN_AUTH_PROVIDER_LOGIN
				+ "&"
				+ Controller.PARAM_AUTH_PROVIDER + "=" + TechcelloAuthProvider.PROVIDER_NAME;
	}
	
	public String getLoginURL() {
		return fullContextPath;
	}

	public void setFullContextPath(String fullContextPath) {
		this.fullContextPath = fullContextPath;
	}

	public void setAuthStatus(Status authStatus) {
		this.authStatus = authStatus;
	}
	
	public boolean isLoginSuccess() {
		return authStatus == Status.SUCCESS;
	}
	
	public boolean isLoginFailure() {
		return authStatus == Status.UNAUTHORIZED;
	}
	
	public boolean isError() {
		return authStatus == Status.ERROR;
	}

	public String getAuthStatusMessage() {
		return authStatusMessage;
	}

	public void setAuthStatusMessage(String authStatusMessage) {
		this.authStatusMessage = authStatusMessage;
	}

	public void setTokenValues(List<String> tokenValueMessagesRows) {
		this.tokenValueMessagesRows = tokenValueMessagesRows;
	}

	public List<String> getTokenValues() {
		return tokenValueMessagesRows;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

}
