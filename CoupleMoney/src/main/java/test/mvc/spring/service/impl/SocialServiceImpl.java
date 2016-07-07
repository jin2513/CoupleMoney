package test.mvc.spring.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import test.mvc.spring.common.code.CommonCode;
import test.mvc.spring.common.handler.SessionHandler;
import test.mvc.spring.module.social.AbstractSocialNetworkService;
import test.mvc.spring.module.social.FactorySocialNetworkService;
import test.mvc.spring.service.SocialService;
import test.mvc.spring.vo.UserVo;

@Service
public class SocialServiceImpl implements SocialService {
	private static final Logger logger = LoggerFactory.getLogger(SocialServiceImpl.class);
	
	@Autowired
	private FactorySocialNetworkService socialNetworkServiceFactory;
	
	@Override
	public String getOauthUrlBySocialType(HttpServletRequest request, String socialType) {
		// 1. 팩토리 생성
		AbstractSocialNetworkService sns = socialNetworkServiceFactory.create(socialType);
		
		// 2. uri와 state 생성
		String redirectUri = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "") + request.getContextPath();
		String state = sns.generateStateToken(socialType);
		logger.info("state : " + state);
		
		return sns.createOAuthAuthorizationURL(request, redirectUri, state);
	}
	
	@Override
	public String getUserInfoByOauth1x(HttpServletRequest request, String socialType, String oauth_token, String oauth_verifier) {
		// 1. 팩토리 생성
		AbstractSocialNetworkService sns = socialNetworkServiceFactory.create(socialType);
		
		UserVo userVo = sns.user(null, oauth_token, oauth_verifier, request);
		SessionHandler.setObjectInfo(request, CommonCode.SessionType.USER.code, userVo);
		
		return "redirect:/";
	}
	
	@Override
	public String getUserInfoByOauth2x(HttpServletRequest request, String socialType, String code, String state) {
		// 1. 팩토리 생성
		AbstractSocialNetworkService sns = socialNetworkServiceFactory.create(socialType);
		
		// 2. 세션에 담긴 state 값 조회
		String storedState = SessionHandler.getStringInfo(request, CommonCode.SessionType.STATE.code);
		
		// 2.1. state 값 인증
		if(!state.equals("") && !state.equals(storedState)) {
			throw new Error("Is not equals state value. [state: " + state + ", storedState: " + storedState + "]");
		} else {
			SessionHandler.removeSessionInfo(request, CommonCode.SessionType.STATE.code);
		}
		
		UserVo userVo = null;
		
		if(!socialType.equals(CommonCode.SocialType.GOOGLE.code)) {
			// 3. token 획득
			Map<String, Object> token = sns.getToken(code, state);
			String accessToken = (String) token.get("access_token");
			
			// 4. 사용자 정보
			userVo = sns.user(accessToken, null, null, null);
		} else {
			userVo = sns.user(code, null, null, request);
		}
		
		SessionHandler.setObjectInfo(request, CommonCode.SessionType.USER.code, userVo);
		
		logger.info(userVo.toString());
		
		return "redirect:/";
	}
}
