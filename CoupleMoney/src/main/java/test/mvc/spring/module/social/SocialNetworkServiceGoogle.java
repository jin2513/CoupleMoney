package test.mvc.spring.module.social;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import test.mvc.spring.common.code.CommonCode;
import test.mvc.spring.common.handler.SessionHandler;
import test.mvc.spring.vo.UserVo;

public class SocialNetworkServiceGoogle extends AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkServiceGoogle.class);
	
	private static final String CLIENT_KEY = "692769889068-fpkdqc40us1b8bn73oi8f1j9qkfpg1nh.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "w3krC0U57frVJ_RGUUhEpqDz";
	private static final String CALLBACK_URL = "/social/google/oauth2.0/callback";
	
	// start google authentication constants
	private Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
	private String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private JsonFactory JSON_FACTORY = new JacksonFactory();
	private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants
	
	private GoogleAuthorizationCodeFlow flow;
	
	public SocialNetworkServiceGoogle() {
		initializeFlow();
	}
	
	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
	 */
	public GoogleAuthorizationCodeFlow initializeFlow() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_KEY, CLIENT_SECRET, (Collection<String>) SCOPE).build();
		
		return flow;
	}
	
	public String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String stateToken) {
		SessionHandler.setStringInfo(request, CommonCode.SessionType.STATE.code, stateToken);
		
		String callbackUrl = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + request.getContextPath() + CALLBACK_URL;
		
		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		
		return url.setRedirectUri(callbackUrl).setState(stateToken).build();
	}
	
	/**
	 * Expects an Authentication Code, and makes an authenticated request for the user's profile information
	 * @return JSON formatted user profile information
	 * @param authCode authentication code provided by google
	 */
	
	public Map<String, Object> getToken(String code, String state) {
		return null;
	}
	
	@Override
	public UserVo user(String authCode, String notUsed, String notUsed2, HttpServletRequest servletRequest) {
		String callbackUrl = servletRequest.getRequestURL().toString().replaceAll(servletRequest.getRequestURI(), "") + servletRequest.getContextPath() + CALLBACK_URL;
		
		try {
			GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(callbackUrl).execute();
			Credential credential = flow.createAndStoreCredential(response, null);
			HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
			// Make an authenticated request
			GenericUrl url = new GenericUrl(USER_INFO_URL);
			HttpRequest request = requestFactory.buildGetRequest(url);
			request.getHeaders().setContentType("application/json");
			String jsonIdentity = request.execute().parseAsString();
			
			logger.info(jsonIdentity);
			
			// 4. parser 객체 생성
			JSONParser jsonParser = new JSONParser();
			
			// 5. string 형태의 json 값 parsing
			JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonIdentity);
			
			// 8. 사용자 정보 map에 저장
//			Map<String, Object> userData = new HashMap<String, Object>();
//			userData.put("id", (String)jsonObject.get("id"));
//			userData.put("email", (String)jsonObject.get("email")); 
//			userData.put("name", (String)jsonObject.get("name"));
//			userData.put("picture", (String)jsonObject.get("picture"));
			UserVo user = new UserVo();
			user.setId((String)jsonObject.get("id"));
			user.setName((String)jsonObject.get("name"));
			user.setProfileImage((String)jsonObject.get("picture"));
			return user;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
