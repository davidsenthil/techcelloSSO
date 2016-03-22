package com.aspire.techcello.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.json.JSONObject;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

/**
 * @author Senthil Krishnamurthy
 * 
 * Handler that communicates with Auth Provider
 */
public class AuthHandler {
	
	enum Status {
		SUCCESS,
		UNAUTHORIZED,
		ERROR
	}

	public static final String ACTION_OPEN_AUTH_PROVIDER_LOGIN = "OpenAuthProviderLogin";
	public static final String ACTION_AUTH_REDIRECT = "AuthRedirect";
	
	private static final String AUTH_STATUS_SUCCESS = "Success";
	private static final String AUTH_STATUS_FAILURE = "Failure/Error";
	private static final String AUTH_STATUS_UNAUTHORIZED = "Not a valid user";
	private static final String ACCESS_TOKEN_PARAM = "access_token";

	final static Logger logger = Logger.getLogger(AuthHandler.class);
	private AuthProvider authProvider;
	private Status authStatus;
	private StringBuilder authStatusMessage = new StringBuilder();
	private List<String> tokenValueMessagesRows = new ArrayList<String>();
	private String userFullName;
	
	
	public AuthHandler(AuthProvider authProvider) {
		this.authProvider = authProvider;
	}

	/**
	 * @return URI for initial Authorize endpoint
	 */
	public String getLocationUri() {
		try {
			OAuthClientRequest oAuthRequest = OAuthClientRequest
					   .authorizationLocation(authProvider.getAuthorizationEndpoint())
					   .setClientId(authProvider.getClientId())
					   .setResponseType("code")
					   .setScope("openid email profile")
					   .setState(generateSecurityIdentifier())
					   .setRedirectURI(authProvider.getRedirectURI())
					   .buildQueryMessage();

			logger.debug("Auth Request URI: \n" + oAuthRequest.getLocationUri());
			return oAuthRequest.getLocationUri();

		} catch (OAuthSystemException e) {
			authStatus = Status.ERROR;
			authStatusMessage.append("\n").append(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	public void processAuthResponse(HttpServletRequest request) throws IOException {
		OAuthAuthzResponse oar = null;
	
		try {
			oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
			String code = oar.getCode();
			logger.debug("Code from Auth provider: " + code);
			// Got the authorization code, next request for token
			requestToken(code, oar);
		} catch (OAuthProblemException e) {
			authStatus = Status.ERROR;
			authStatusMessage.append("\n").append(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Get openId token
	 */
	private void requestToken(String code, OAuthAuthzResponse authResponse) throws ParseException, IOException {
		HttpPost request = authProvider.getTokenRequest(code, authResponse);
		logger.debug("Token request: " + request.toString());
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(request);
		String jsonResponse = EntityUtils.toString(response.getEntity());
	
		// Convert token string into JSON object
		JSONObject tokenObject = new JSONObject(jsonResponse);
		String accessToken = tokenObject.getString(ACCESS_TOKEN_PARAM);
		String idToken = tokenObject.getString(authProvider.getIdTokenParam());
		tokenValueMessagesRows.add("<b><u>JSON Web Token (JWT)</u></b>: " + idToken+"<p>");
		logger.debug("Id Token: " + idToken);
		logger.debug("Access Token: " + accessToken);

		// Read the JWT
		readIdToken(idToken);
		
		authStatus = idToken != null ? Status.SUCCESS : Status.UNAUTHORIZED;
		
		// Get user info
		requestUserInfo(accessToken);
	}
	

	private void requestUserInfo(String accessToken) {
		if (authProvider.getProfileEndpoint() == null) {
			// No profile details API
			return;
		}
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();  
            HttpGet httpGet = new HttpGet(authProvider.getProfileEndpoint());
            httpGet.addHeader("Authorization", "Bearer "  + accessToken);
            
            HttpResponse response = httpclient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            logger.debug("User Info response code: " + responseCode);
            String reponseString = EntityUtils.toString(response.getEntity());
			
            Map<String, Object> responseMap = JSONUtils.parseJSON(reponseString);
			String userName = (String) responseMap.get("displayName");
			userFullName = (String)responseMap.get("givenName") + " " + (String) responseMap.get("familyName");
			String gender = (String) responseMap.get("gender");
			if (userFullName != null) {
				tokenValueMessagesRows.add("Full Name: " + userFullName);
			}
			if (userName != null) {
				tokenValueMessagesRows.add("UserName: " + userName);
			}
			if (gender != null) {
				tokenValueMessagesRows.add("Gender: " + gender);
			}
		} catch (ClientProtocolException e) {
			authStatus = Status.ERROR;
			authStatusMessage.append("\n").append(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			authStatus = Status.ERROR;
			authStatusMessage.append("\n").append(e.getMessage());
			e.printStackTrace();
		}
	}

	private void readIdToken(String idToken) {
		// Decode JWT and read values
		Jwt decodedValue = JwtHelper.decode(idToken);
		logger.debug("JWT string: " + idToken);
		logger.debug("Id token JWT decoded value: " + decodedValue);
		JSONObject idTokenValues = new JSONObject(decodedValue.getClaims());
		if (idTokenValues.length() > 0) {
			tokenValueMessagesRows.add("<b><u>ID Token values: </u></b>");
		}
		for (Object key : idTokenValues.keySet()) {
			String valueRow = "<u>" + key +"</u>" + ": " + idTokenValues.get((String) key);
			tokenValueMessagesRows.add(valueRow);
		}		
	}

	public String getAuthStatusMessage() {
		StringBuilder authenticationStatusMessage = new StringBuilder("Authentication Status : ");
		switch (authStatus) {
		case SUCCESS :
			authenticationStatusMessage.append(AUTH_STATUS_SUCCESS);
			break;
		case UNAUTHORIZED:
			authenticationStatusMessage.append(AUTH_STATUS_UNAUTHORIZED);
			break;
		default:
			authenticationStatusMessage.append(AUTH_STATUS_FAILURE);
		}
		return authStatusMessage + "\n" + authenticationStatusMessage.toString();
	}
	
	public Status getAuthStatus() {
		return authStatus;
	}
	
	private String generateSecurityIdentifier() {
		return UUID.randomUUID().toString();
	}

	public List<String> getTokenValueMessagesRows() {
		return tokenValueMessagesRows;
	}

	public String getUserFullName() {
		return userFullName;
	}

}
