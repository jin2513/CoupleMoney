package test.mvc.spring.module.social;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.mvc.spring.common.code.CommonCode;
import test.mvc.spring.common.handler.SessionHandler;
import test.mvc.spring.vo.UserVo;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class SocialNetworkServiceTwitter extends AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkServiceTwitter.class);
	
	private static final String TWITTER_CONSUMER_KEY = "WacJDgTwnYr0JtZqKlMbbeYPO";
	private static final String TWITTER_CONSUMER_SECRET = "lLdwg5064ezYuTo6QhF5JV3yefJeq4z5e0tHNeHrtF52foUmFI";
	
	public final String REQUEST_TOKEN ="requestToken";
	
	private Twitter twitter;
	private RequestToken requestToken = null;
	
	public SocialNetworkServiceTwitter() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			throw new Error(e.getMessage());
		}
	}
	
	public Twitter getInstance() {
		return twitter;
	}
	
	private void setOAuth() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
	}
	
	public void setRequestTokenSession(HttpServletRequest request) {
		setOAuth();
		
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch(TwitterException te) {
			te.printStackTrace();
		}
		
		SessionHandler.setObjectInfo(request, REQUEST_TOKEN, requestToken);
	}
	
	public boolean isRequesetToken(HttpServletRequest request) {
		this.requestToken = (RequestToken)SessionHandler.getObjectInfo(request, REQUEST_TOKEN);
		
		if(requestToken != null) {
			logger.info("facebook session is not null!");
			return true;
		} else {
			logger.info("facebook session is null!");
			return false;
		}
	}
	
	public String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String state) {
		SessionHandler.setStringInfo(request, CommonCode.SessionType.STATE.code, state);
		setRequestTokenSession(request);
		return requestToken.getAuthorizationURL();
	}
	
	@Override
	public Map<String, Object> getToken(String code, String state) {
		return null;
	}
	
	@Override
	public UserVo user(String notUsed, String oauth_token, String oauth_verifier, HttpServletRequest request) {
		this.requestToken = (RequestToken)SessionHandler.getObjectInfo(request, REQUEST_TOKEN);
		
		if(requestToken == null) {
			throw new Error("requestToken is null");
		}
		if(oauth_token == null) {
			throw new Error("oauthToken is null");
		}
		
		if(!requestToken.getToken().equals(oauth_token)) {
			throw new Error("oauthToken is not equals");
		}
		
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
			User showUser = twitter.showUser(accessToken.getUserId());
			
			logger.info(showUser.toString());
			
//			Map<String, Object> userData = new HashMap<String, Object>();
//			userData.put("userid", String.valueOf(user.getId()));
//			userData.put("name", user.getName());
//			userData.put("profileImageUrl", user.getProfileImageURL());
			
			UserVo user = new UserVo();
			user.setId(String.valueOf(showUser.getId()));
			user.setName(showUser.getName());
			user.setProfileImage(showUser.getProfileImageURL());
			return user;
		} catch (TwitterException e) {
			throw new Error("ErrorCode: " + e.getErrorCode() + ", message: " + e.getErrorMessage() + "]");
		}
	}
}
