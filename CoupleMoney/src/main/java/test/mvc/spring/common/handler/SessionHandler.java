package test.mvc.spring.common.handler;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHandler {
	public static String STATE = "state";
	
	private SessionHandler() {
		
	}
	
	private static HttpSession getSession(HttpServletRequest request) {
		return request.getSession(true);
	}
	
	public static void invalidateSession(HttpServletRequest request) {
		HttpSession httpSession = getSession(request);
		for (Enumeration<?> er = httpSession.getAttributeNames(); er.hasMoreElements(); httpSession.removeAttribute((String) er.nextElement()));
		httpSession.invalidate();
	}
	
	public static void removeSessionInfo(HttpServletRequest request, String key) {
		HttpSession httpSession = getSession(request);
		httpSession.removeAttribute(key);
	}
	
	public static void setObjectInfo(HttpServletRequest request, String key, Object value) {
		HttpSession httpSession = getSession(request);
		httpSession.setAttribute(key, value);
	}
	
	public static Object getObjectInfo(HttpServletRequest request, String key) {
		HttpSession httpSession = getSession(request);
		return httpSession.getAttribute(key);
	}
	
	public static void setStringInfo(HttpServletRequest request, String key, String value) {
		HttpSession httpSession = getSession(request);
		httpSession.setAttribute(key, value);
	}
	
	public static String getStringInfo(HttpServletRequest request, String key) {
		HttpSession httpSession = getSession(request);
		return (String) httpSession.getAttribute(key);
	}
}