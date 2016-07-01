package test.mvc.spring.service;

import javax.servlet.http.HttpServletRequest;

public interface SocialService {
	public String getOauthUrlBySocialType(HttpServletRequest request, String socialType);
	public String getUserInfoByOauth1x(HttpServletRequest request, String socialType, String oauth_token, String oauth_verifier);
	public String getUserInfoByOauth2x(HttpServletRequest request, String socialType, String code, String state);
}
