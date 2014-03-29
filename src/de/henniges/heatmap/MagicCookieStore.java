package de.henniges.heatmap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class MagicCookieStore extends BasicCookieStore {

	public void grabCookies(HttpResponse res) {
		Header header = res.getFirstHeader("Set-Cookie");
		String cc = header.getValue();
		
		final String searchTerm = "csrftoken=";
		int start = cc.indexOf(searchTerm);
		String tokenstr = cc.substring(start+searchTerm.length());
		int stop = tokenstr.indexOf(";");
		String token = tokenstr.substring(0, stop);
		addCookie(new BasicClientCookie(searchTerm, token));
	}
	
}
