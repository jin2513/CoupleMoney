package test.mvc.spring.module.social;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.mvc.spring.common.code.CommonCode;
import test.mvc.spring.common.handler.SessionHandler;
import test.mvc.spring.vo.UserVo;

public class SocialNetworkServiceNaver extends AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkServiceNaver.class);
	
	private static final String NAVER_AUTH_HOST = "https://nid.naver.com";
	private static final String NAVER_API_HOST = "https://openapi.naver.com";
	private static final String NAVER_CLIENT_KEY = "yARxDvyeS0nTwisjXTHJ";
	private static final String NAVER_CLIENT_SECRET = "DItYX6E4Qg";
	private static final String NAVER_CLIENT_CALLBACK = "/social/naver/oauth2.0/callback";
	
	public String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String state) {
		SessionHandler.setStringInfo(request, CommonCode.SessionType.STATE.code, state);
		return NAVER_AUTH_HOST + "/oauth2.0/authorize?client_id=" + NAVER_CLIENT_KEY + "&response_type=code&redirect_uri=" + redirectUri + NAVER_CLIENT_CALLBACK +"&state=" + state;
	}
	
	public Map<String, Object> getToken(String code, String state) {
		// 1. url 정보 
		String requestUrl = NAVER_AUTH_HOST + "/oauth2.0/token";
		
		// 2. 헤더 정보
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 3. 바디 정보
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "authorization_code");
		params.put("client_id", NAVER_CLIENT_KEY);
		params.put("client_secret", NAVER_CLIENT_SECRET);
		params.put("code", code);
		params.put("state", state);
		
		// 4. accessType 결과
		return tokenJsonConvertByMap(httpPost(requestUrl, headers, params));
	}
	
	@Override
	public UserVo user(String accessToken, String notUsed, String notUsed2, HttpServletRequest servletRequest) {
		logger.info(accessToken);
		// 1. url 정보 
		String url = NAVER_API_HOST + "/v1/nid/me";
		
		// 2. 헤더 정보
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + accessToken);
		
		try {
			// 4. json 형태의 결과값
			String result = httpGet(url, headers, null);
			logger.info(result);
			
			// 5. parser 객체 생성
			JSONParser jsonParser = new JSONParser();
			
			// 6. string 형태의 json 값 parsing
			JSONObject resultJsonObject = (JSONObject)jsonParser.parse(result.toString());
			
			// 7. resultcode 확인
			String resultCode = (String)resultJsonObject.get("resultcode");
			String message = (String)resultJsonObject.get("message");
			if(!resultCode.equals("00")) {
				throw new Error("[Naver] User info api resultcode error.[resultcode: " + resultCode + " / msg: " + message + "]");
			}
			
			// 8. response 결과
			JSONObject response = (JSONObject) resultJsonObject.get("response");
			if(response == null) {
				throw new Error("[Naver] User info api error.");
			}
			
			// 8. 사용자 정보 map에 저장
			UserVo user = new UserVo();
			user.setId((String)response.get("id"));
			user.setName((String)response.get("name"));
			user.setProfileImage((String)response.get("profile_image"));
			return user;
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}
}
