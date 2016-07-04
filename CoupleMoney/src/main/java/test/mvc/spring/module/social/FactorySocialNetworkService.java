package test.mvc.spring.module.social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FactorySocialNetworkService {
	
	@Value("${daum.host}")
	private String DAUM_HOST;
	@Value("${daum.client.id}")
	private String DAUM_CLIENT_KEY;
	@Value("${daum.client.secret}")
	private String DAUM_CLIENT_SECRET;
	@Value("${daum.client.callback}")
	private String DAUM_CLIENT_CALLBACK;

	public AbstractSocialNetworkService create(String socialType) {
		switch (socialType) {
		case "facebook":
			break;
		case "google":
			return new SocialNetworkServiceGoogle();
		case "kakao":
			return new SocialNetworkServiceKakao();
		case "daum":
			return new SocialNetworkServiceDaum(DAUM_HOST, DAUM_CLIENT_KEY, DAUM_CLIENT_SECRET, DAUM_CLIENT_CALLBACK);
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
