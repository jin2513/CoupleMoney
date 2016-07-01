package test.mvc.spring.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import test.mvc.spring.service.impl.SocialServiceImpl;

@Controller
@RequestMapping(value = "/social")
public class SocialController {
	@Autowired
	private SocialServiceImpl socialServiceImpl;
	
	@RequestMapping(value = "/oauth/{socialType}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> returnOauthUrlBySocialType(HttpServletRequest request, @PathVariable("socialType") String socialType) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("url", socialServiceImpl.getOauthUrlBySocialType(request, socialType));
		return param;
	}
	
	@RequestMapping(value = "/{socialType}/{oauthType}/callback")
	public String getSocialCallback(HttpServletRequest request,
			@PathVariable("socialType") String socialType,
			@PathVariable("oauthType") String oauthType,
			@RequestParam(value="code", required=false, defaultValue="") String code,
			@RequestParam(value="state", required=false, defaultValue="")String state,
			@RequestParam(value="oauth_token", required=false, defaultValue="") String oauth_token,
			@RequestParam(value="oauth_verifier", required=false, defaultValue="") String oauth_verifier,
			@RequestParam(value="error", required=false, defaultValue="") String error,
			@RequestParam(value="error_description", required=false, defaultValue="") String error_description) {
		switch (oauthType) {
		case "oauth1.0a":
			return socialServiceImpl.getUserInfoByOauth1x(request, socialType, oauth_token, oauth_verifier);
		case "oauth2.0":
		default:
//			if ("naver".equals(socialType)) {
//				if(!error.equals("")) {
//					throw new Error("[error: " + error + ", description: " + error_description);
//				}
//			}
			return socialServiceImpl.getUserInfoByOauth2x(request, socialType, code, state);
		}
	}
}
