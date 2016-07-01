package test.mvc.spring.module.social;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import test.mvc.spring.common.handler.SessionHandler;

public class SocialNetworkServiceDaum extends AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkServiceDaum.class);
	
	private static final String DAUM_HOST = "https://apis.daum.net";
	private static final String DAUM_CLIENT_KEY = "5710146924007080108";
	private static final String DAUM_CLIENT_SECRET = "95de935ac3accea52d0295de9eab5fa8";
	private static final String DAUM_CLIENT_CALLBACK = "/social/daum/oauth2.0/callback";
	
	public String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String state) {
		SessionHandler.setStringInfo(request, SessionHandler.STATE, state);
		return DAUM_HOST + "/oauth2/authorize?client_id=" + DAUM_CLIENT_KEY + "&redirect_uri=" + redirectUri + DAUM_CLIENT_CALLBACK + "&response_type=code";
	}
	
	public Map<String, Object> getToken(String code, String state) {
		// 1. url 정보 
		String requestUrl = DAUM_HOST + "/oauth2/token";
		
		// 2. 헤더 정보
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 3. 바디 정보
		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", DAUM_CLIENT_KEY);
		params.put("client_secret", DAUM_CLIENT_SECRET);
		params.put("redirect_uri", DAUM_CLIENT_CALLBACK);
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		
		// 4. accessType 결과
		return tokenJsonConvertByMap(httpPost(requestUrl, headers, params));
	}
	
	@Override
	public Map<String, Object> user(String accessToken, String oauth_token, String oauth_verifier, HttpServletRequest servletRequest) {
		// 1. url 정보
		String url = DAUM_HOST + "/user/v1/show.json";
		
		// 2. 바디 정보
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("output", "json");
		
		try {
			// 3. json 형태의 결과값
			String result = httpGet(url, null, params);
			logger.debug(result);
			
			// 4. parser 객체 생성
			JSONParser jsonParser = new JSONParser();
			
			// 5. string 형태의 json 값 parsing
			JSONObject jsonObject = (JSONObject)jsonParser.parse(result.toString());
			
			// 6. 코드값 확인. 200이 아니면 에러 처리
			long code = (long)jsonObject.get("code");
			if(!HttpStatus.valueOf((int) code).is2xxSuccessful()) {
				throw new Error("[Daum] User info api error.[error_code: " + code + ", message: " + (String)jsonObject.get("message"));
			}
			
			// 7. 결과 정보 파싱
			JSONObject resultJsonObject = (JSONObject) jsonObject.get("result");
			
			// 8. 사용자 정보 map에 저장
			Map<String, Object> userData = new HashMap<String, Object>();
			userData.put("userid", (String)resultJsonObject.get("userid")); 
			userData.put("id", (long)resultJsonObject.get("id"));
			userData.put("nickname", (String)resultJsonObject.get("nickname"));
			userData.put("imagepath", (String)resultJsonObject.get("imagepath"));
			userData.put("bigImagePath", (String)resultJsonObject.get("bigImagePath"));
			
			return userData;
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}
}
