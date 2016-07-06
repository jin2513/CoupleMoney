package test.mvc.spring.module.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import test.mvc.spring.vo.UserVo;

@SuppressWarnings("deprecation")
public abstract class AbstractSocialNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSocialNetworkService.class);
	
	/**
	 * 인증 url 생성
	 * @param redirectUri
	 * @param state
	 * @return
	 */
	public abstract String createOAuthAuthorizationURL(HttpServletRequest request, String redirectUri, String stateToken);
	/**
	 * 토큰 정보 획득
	 * @param code
	 * @param state
	 * @return
	 */
	public abstract Map<String, Object> getToken(String code, String state);
	
	/**
	 * json 형태의 token 값을 map 형태로 변환
	 * @param json
	 * @return
	 */
	public Map<String, Object> tokenJsonConvertByMap(String json) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(json);
			tokenMap.put("access_token", jsonObject.get("access_token"));
			tokenMap.put("refresh_token", jsonObject.get("refresh_token"));
			tokenMap.put("token_type", jsonObject.get("token_type"));
			tokenMap.put("expires_in", jsonObject.get("expires_in"));
			return tokenMap;
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * 사용자 정보
	 * @param accessToken
	 * @return
	 */
	public abstract UserVo user(String accessToken, String oauth_token, String oauth_verifier, HttpServletRequest servletRequest);
	
	/**
	 * Generates a secure state token
	 * @return
	 */
	public String generateStateToken(String socialType) {
		SecureRandom random = new SecureRandom();
		return socialType + ";" + random.nextInt();
	}
	
	/**
	 * GET 방식 
	 * @param url
	 * @param params
	 * @return
	 */
	protected String httpGet(String url, Map<String, String> headers, Map<String, String> params) {
		int index = 0;
		StringBuffer temp = new StringBuffer();
		
		for(Entry<String, String> entry : params.entrySet()) {
			if(index == 0) {
				temp.append("?" + entry.getKey() + "=" + entry.getValue());
			} else {
				temp.append("&" + entry.getKey() + "=" + entry.getValue());
			}
			
			index++;
		}
		
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url + temp.toString());
		
		StringBuffer result = new StringBuffer();
		
		try {
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();

			// 응답 결과
			if (entity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"), 8);
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(new String(URLDecoder.decode(line, "UTF-8")));
				}
				
				logger.info(result.toString());
				
				return result.toString();
			}
			
			get.abort();
			httpclient.getConnectionManager().shutdown();
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		logger.info(result.toString());
		
		return result.toString();
	}
	
	/**
	 * POST 방식
	 * @param url
	 * @param header
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected String httpPost(String url, Map<String, String> header, Map<String, String> params) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		// 1. 헤더값 설정
		if(header != null) {
			Iterator<String> it = header.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				String val = header.get(key);
				post.addHeader(key, val);
			}
		}
		
		// 2. 파라미터값 설정
		if(params != null) {
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			Iterator<String> itParam = params.keySet().iterator();
			while(itParam.hasNext()){
				String key = itParam.next();
				String val = params.get(key);
				
				urlParameters.add(new BasicNameValuePair(key, val));
			}
			
			try {
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
			} catch (UnsupportedEncodingException e) {
				throw new Error(e.getMessage());
			}
		}
		
		// 3. 응답결과
		try {
			HttpResponse res = client.execute(post);
			
			int statusCode = res.getStatusLine().getStatusCode();
			
			if(!HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
				throw new Error("Response Error. [" + statusCode + "] - " + HttpStatus.valueOf(statusCode).name());
			} else {
				BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "utf-8"), 8);
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(new String(URLDecoder.decode(line, "UTF-8")));
				}
				
				logger.info(result.toString());
				
				return result.toString();
			}
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}
}
