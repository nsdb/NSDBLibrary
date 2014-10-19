package com.nsdb.cm.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpUtils {
	
	public static InputStreamReader getUTF8StreamFromRequest(HttpUriRequest request, String charSet) throws Exception {
		return getStreamFromRequest(request,"utf-8");
	}

	public static InputStreamReader getStreamFromRequest(HttpUriRequest request, String charSet) throws Exception {

		HttpClient client=new DefaultHttpClient();

		// cookie load
//		CookieStore cookieStore=((DefaultHttpClient)client).getCookieStore();
//		List<Cookie> cookieList=cookieStore.getCookies();
//		String cookieName=AppPref.getString("cookieName");
//		if(cookieList.size()==0 && cookieName.compareTo("")!=0) {
//			String cookieValue=AppPref.getString("cookieValue");
//			String cookieDomain=AppPref.getString("cookieDomain");
//			String cookiePath=AppPref.getString("cookiePath");
//			BasicClientCookie cookie=new BasicClientCookie( cookieName,cookieValue );
//			cookie.setDomain(cookieDomain);
//			cookie.setPath(cookiePath);
//			cookieStore.addCookie(cookie);
//		}
		
		// get response
		HttpResponse response=client.execute(request);
		InputStream is=response.getEntity().getContent();
		InputStreamReader isr=new InputStreamReader(is,charSet);

		// cookie save
//		if(cookieList.size()>0) {
//			AppPref.setString("cookieName",cookieList.get(0).getName());
//			AppPref.setString("cookieValue",cookieList.get(0).getValue());
//			AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
//			AppPref.setString("cookiePath",cookieList.get(0).getPath());
//		}
		
		return isr;
		
	}
	
	
}
