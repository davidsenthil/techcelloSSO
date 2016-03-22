package com.aspire.techcello.handlers;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

public class TechcelloAuthProvider extends AuthProvider {

	public static String PROVIDER_NAME = "techcello";
//	// These endpoints should be moved be property/config file (if any)
//	private static String AUTHORIZATION_END_POINT = "http://cello46Auth.azurewebsites.net/authorize";
//	private static String TOKEN_END_POINT = "http://cello46Auth.azurewebsites.net/api/token";
//	private static String PROFILE_END_POINT = "http://cello46Auth.azurewebsites.net/api/userinfo";
	
	private static String AUTHORIZATION_END_POINT = "http://54.208.118.26/authorize";
	private static String TOKEN_END_POINT = "http://54.208.118.26/api/token";
	private static String PROFILE_END_POINT = "http://54.208.118.26/api/userinfo";
	
	private static String CLIENT_ID = "5df78e31-1dca-4dcd-832f-c28b599fff5b";
	private static String REDIRECT_URI = "http://localhost:8080/techcello/Controller/callback";
	private static String ID_TOKEN_PARAM = "identity_token";
	private final static Logger logger = Logger.getLogger(AuthHandler.class);
	
	@Override
	public String getName() {
		return PROVIDER_NAME;
	}

	@Override
	public String getClientId() {
		return CLIENT_ID;
	}

	@Override
	public String getClientSecret() {
		return "";
	}

	@Override
	public String getTokenEndpoint() {
		return TOKEN_END_POINT;
	}

	@Override
	public String getAuthorizationEndpoint() {
		return AUTHORIZATION_END_POINT;
	}

	@Override
	public String getProfileEndpoint() {
		return PROFILE_END_POINT + "?client_id=" + CLIENT_ID;
	}

	@Override
	public String getRedirectURI() {
		return REDIRECT_URI;
	}

	@Override
	public HttpPost getTokenRequest(String code, OAuthAuthzResponse authResponse) throws UnsupportedEncodingException {
		HttpPost request = new HttpPost(getTokenEndpoint());
		String clientId  = authResponse.getParam("client_id");
		logger.debug("Client Id: " + clientId);
		
		String companyCode = authResponse.getParam("companycode");
		logger.debug("company code: " + companyCode);
		
		// JSON request
		String jsonString = "{\"code\":\""+ code+"\","
				+ "\"companycode\":\""+ companyCode +"\","
				+ "\"client_id\":\""+ clientId +"\","
				+ "\"grant_type\":\""+ GrantType.AUTHORIZATION_CODE.toString() +"\","
				+ "\"redirect_uri\":\""+ getRedirectURI() +"\""
				+ "} ";
		logger.debug("Request (JSON) string: " + jsonString);
		StringEntity params =new StringEntity(jsonString, ContentType.APPLICATION_JSON);
		
		request.setEntity(params);
		request.addHeader("content-type", "application/json");
		
		return request;
	}

	@Override
	public String getIdTokenParam() {
		return ID_TOKEN_PARAM;
	}
}
