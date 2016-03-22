package com.aspire.techcello.handlers;


import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;

import com.aspire.techcello.controller.Controller;

public abstract class AuthProvider {

	public abstract String getName();
	public abstract String getClientId();
	public abstract String getClientSecret();
	public abstract String getTokenEndpoint();
	public abstract String getAuthorizationEndpoint();
	public abstract String getProfileEndpoint();
	public abstract HttpPost getTokenRequest(String code, OAuthAuthzResponse authResponse) throws UnsupportedEncodingException;
	
	// token param string in the token response
	public String getIdTokenParam() {
		return "id_token";
	}
	
	public String getRedirectURI() {
		// TODO shouldn't this be the server URL ??
		return "http://localhost:8080/techcello/Controller?"
				+ Controller.PARAM_AUTH_PROVIDER + "=" + getName()
				+ "&"
				+ Controller.PARAM_ACTION+"="+ AuthHandler.ACTION_AUTH_REDIRECT;
	}
	
}
