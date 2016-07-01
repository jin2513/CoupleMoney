package test.mvc.spring.module.social;

import org.springframework.stereotype.Service;

@Service
public class FactorySocialNetworkService {

	public AbstractSocialNetworkService create(String socialType) {
		switch(socialType) {
		case "facebook":
			break;
		case "google":
			return new SocialNetworkServiceGoogle();
		case "kakao":
			return new SocialNetworkServiceKakao();
		case "daum":
			return new SocialNetworkServiceDaum();
		case "naver":
			return new SocialNetworkServiceNaver();
		case "twitter":
			return new SocialNetworkServiceTwitter();
		default:
			break;
		}
		return null;
	}
}
