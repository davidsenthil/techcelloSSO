package com.aspire.techcello.handlers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.message.types.GrantType;

public class GoogleAuthProvider extends AuthProvider {

	// Move these constants to property file
	private static final String GOOGLE_CLIENT_ID = "13649777523-1jfkjrcq0s6bt2qon1ihom49dv2bkgcn.apps.googleusercontent.com";
	private static final String GOOGLE_CLIENT_SECRET = "Z7zaCF6T-wXFKErQZ-aDpWjT";
	
	@Override
	public String getName() {
		return OAuthProviderType.GOOGLE.toString();
	}

	@Override
	public String getClientId() {
		return GOOGLE_CLIENT_ID;
	}

	@Override
	public String getClientSecret() {
		return GOOGLE_CLIENT_SECRET;
	}

	@Override
	public String getTokenEndpoint() {
		return OAuthProviderType.GOOGLE.getTokenEndpoint();
	}

	@Override
	public String getAuthorizationEndpoint() {
		return OAuthProviderType.GOOGLE.getAuthzEndpoint();
	}

	@Override
	public String getProfileEndpoint() {
		return "https://www.googleapis.com/oauth2/v3/userinfo";
	}

	@Override
	public HttpPost getTokenRequest(String code, OAuthAuthzResponse authResponse) throws UnsupportedEncodingException {
		HttpPost request = new HttpPost(getTokenEndpoint());
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("code", code));
		urlParameters.add(new BasicNameValuePair("grant_type", GrantType.AUTHORIZATION_CODE.toString()));
		urlParameters.add(new BasicNameValuePair("redirect_uri", getRedirectURI()));
		request.setEntity(new UrlEncodedFormEntity(urlParameters));

		String auth = getClientId() + ":" + getClientSecret();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		
		return request;
	}

}
