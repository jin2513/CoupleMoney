package test.mvc.spring.common.code;

public class CommonCode {
	public enum SocialType {

		DAUM("daum")
		, KAKAO("kakao")
		, NAVER("naver")
		, GOOGLE("google")
		;
		
		public String code;
		
		SocialType(String code) {
			this.code = code;
		}
	}
	
	public enum SessionType {
		USER("user"), STATE("state");
		
		public String code;
		
		SessionType(String code) {
			this.code = code;
		}
	}
}
