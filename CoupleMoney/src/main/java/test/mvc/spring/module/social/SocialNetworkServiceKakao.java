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

public class SocialNetworkServiceKakao extends AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkServiceKakao.class);
	
	private static final String KAKAO_AUTH_HOST = "https://kauth.kakao.com";
	private static final String KAKAO_API_HOST = "https://kapi.kakao.com";
	private static final String KAKAO_CLIENT_ID = "66d8c42a4ca66c04e317cb2669ac0e4d";
	private static final String KAKAO_CLIENT_CALLBACK = "/social/kakao/oauth2.0/callback";
	
	public String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String state) {
		SessionHandler.setStringInfo(request, CommonCode.SessionType.STATE.code, state);
		return KAKAO_AUTH_HOST + "/oauth/authorize?client_id=" + KAKAO_CLIENT_ID + "&response_type=code&redirect_uri=" +redirectUri + KAKAO_CLIENT_CALLBACK +"&state=" + state;
	}

	@Override
	public Map<String, Object> getToken(String code, String state) {
		// 1. url 정보 
		String requestUrl = KAKAO_AUTH_HOST + "/oauth/token";
		
		// 2. 헤더 정보
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 3. 바디 정보
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "authorization_code");
		params.put("client_id", KAKAO_CLIENT_ID);
		params.put("code", code);
		params.put("state", state);
		
		// 4. accessType 결과
		return tokenJsonConvertByMap(httpPost(requestUrl, headers, params));
	}
	
	@Override
	public UserVo user(String accessToken, String notUsed, String notUsed2, HttpServletRequest servletRequest) {
		// 1. 요청 url
		String url = KAKAO_API_HOST + "/v1/user/me";
		
		// 2. 헤더 정보
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.put("Authorization", "Bearer " + accessToken);
		
		try {
			// 3. json 형태의 결과값
			String result = httpGet(url, headers, null);
			logger.debug(result);
			
			// 4. parser 객체 생성
			JSONParser jsonParser = new JSONParser();
			
			// 5. string 형태의 json 값 parsing
			JSONObject jsonObject = (JSONObject)jsonParser.parse(result.toString());
			
			// 6. 사용자 정보 map에 저장
			Map<String, Object> userData = new HashMap<String, Object>();
			userData.put("id", (long)jsonObject.get("id"));
			
			JSONObject propertiesJsonObject = (JSONObject) jsonObject.get("properties");
//			userData.put("nickname", (String)propertiesJsonObject.get("nickname"));
//			userData.put("thumbnail_image", (String)propertiesJsonObject.get("thumbnail_image"));
//			userData.put("profile_image", (String)propertiesJsonObject.get("profile_image"));
			UserVo user = new UserVo();
			user.setId(String.valueOf((long)jsonObject.get("id")));
			user.setName((String)propertiesJsonObject.get("nickname"));
			user.setProfileImage((String)propertiesJsonObject.get("profile_image"));
			return user;
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}
}
