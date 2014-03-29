package de.henniges.heatmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {

	String username = "robin.henniges@42reports.com";
	String host = "staging.42reports.com";
	String password = "meepa5Oo";
	String urlBasePath = "https://staging.42reports.com/api-auth/login/?next=/api/v2/sensors/";
	String urlReqPath = "https://staging.42reports.com/api/v2/sensors/";
	DefaultHttpClient client;
	HttpHost targetHost = new HttpHost(host, 443, "https");
	HeatmapView heatview;
    CookieStore cookieStore = new BasicCookieStore();


    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 
		heatview = (HeatmapView) findViewById(R.id.heatmapview);
		heatview.setOnTouchListener(otl);
		//heatview.setStore(store);
		
		client = new DefaultHttpClient();
		prepairCookieStore(client);
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {

				try {
					
				// get token and cookies
				prepair(urlBasePath);
				
				Header[] cookies = login(urlBasePath);
				HttpGet get = new HttpGet(urlReqPath);
				//get.setHeaders(cookies);

				get.setHeader("Content-Type", "application/json");

				HttpResponse response = execute(get);
				
				for(Header h :response.getAllHeaders()){
					System.out.println("HEADERS: " +h.toString());
				}
				
				for(Cookie c :client.getCookieStore().getCookies()){
					System.out.println("Cookie: "+c);
				}
				
				
				HttpEntity entity = response.getEntity();
				Object content = EntityUtils.toString(entity);
				Log.d("EHHEjHE", "OK: " + content.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		th.start();
		
	}
	
	private void prepairCookieStore(DefaultHttpClient client) {
		
		client.setCookieStore(cookieStore);
		
		CookieSpecFactory csf = new CookieSpecFactory() {
	          public CookieSpec newInstance(HttpParams params) {
	              return new BrowserCompatSpec() {
	                  @Override
	                  public void validate(Cookie cookie, CookieOrigin origin)
	                  throws MalformedCookieException {
	                	// not filtering; accept all cookies
	                    Log.d(this.toString(),"cookies");
	                  }
	              };
	          }
	      };
	      client.getCookieSpecs().register("all", csf);
	      client.getParams().setParameter(
	           ClientPNames.COOKIE_POLICY, "all"); 
	}

	
	private HttpResponse execute(HttpRequestBase req) throws ClientProtocolException, IOException {
		BasicHttpContext localContext = new BasicHttpContext();
		BasicScheme basicAuth = new BasicScheme();
		localContext.setAttribute("preemptive-auth", basicAuth);		
		return client.execute(targetHost, req, localContext);
	}
	
	private void prepair(String loginUri) throws ClientProtocolException, IOException{
		HttpGet get = new HttpGet(loginUri);
		HttpResponse res = execute(get);
		res.getEntity().consumeContent();	
	}
	
	private Header[] login(String uri) throws ClientProtocolException, IOException {
		HttpPost loginPost = new HttpPost(uri);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		HttpParams params = new BasicHttpParams();

		loginPost.setHeader("Referer", "https://staging.42reports.com/api-auth/login/?next=/api/v2/sensors/");
		loginPost.setHeader("Host", "staging.42reports.com");
		for(Cookie c :cookieStore.getCookies()){
			if(!loginPost.containsHeader("X-CSRFToken")){
				loginPost.setHeader("X-CSRFToken", c.getValue());
				nameValuePairs.add(new BasicNameValuePair("X-CSRFToken", c.getValue()));
				//params.setParameter("X-CSRFToken", c.getValue());
			}
		}
		
		
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		params.setParameter("username", username);
		params.setParameter("password", password);
		
		
		loginPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		loginPost.setParams(params);
				
		HttpResponse res = execute(loginPost);
		
		return res.getHeaders("Set-Cookie");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private String convertStreamToString(InputStream instream)
			throws IOException {
		InputStreamReader a = new InputStreamReader(instream);
		BufferedReader bf = new BufferedReader(a);
		StringBuilder sb = new StringBuilder();

		String read = bf.readLine();

		while (read != null) {
			sb.append(read);
			read = bf.readLine();
		}
		return sb.toString();
	}
	
	View.OnTouchListener otl = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int size = (int) (5+Math.random()*60);
			heatview.getStore().addHeatPoint((int)event.getX(), (int)event.getY(), size);

			return false;
			
		}
	};

}
