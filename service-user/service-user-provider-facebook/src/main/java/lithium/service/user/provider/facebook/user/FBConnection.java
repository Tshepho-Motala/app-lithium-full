package lithium.service.user.provider.facebook.user;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FBConnection {
	
	@Autowired
	@Qualifier("lithium.service.user.provider.facebook.resttemplate")
	private RestTemplate restTemplate;
	public static final String REDIRECT_URI = "http://localhost:8080/Facebook_Login/fbhome";
	
	public String getFBAuthUrl(String appId) throws Exception {
		String fbLoginUrl = "http://www.facebook.com/dialog/oauth"+
			"?client_id="+appId+
			"&redirect_uri="+URLEncoder.encode(FBConnection.REDIRECT_URI, "UTF-8")+
			"&scope=email";
		return fbLoginUrl;
	}
	
	public String getAccessToken(String code, String appId, String appSecret) throws Exception {
		String accessToken = null;
		String fbGraphURL = "https://graph.facebook.com/oauth/access_token";
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fbGraphURL)
			.queryParam("client_id", appId)
			.queryParam("client_secret", appSecret)
			.queryParam("code", code)
			.queryParam("redirect_uri", URLEncoder.encode("http://localhost:9000/service-user/users/auth?provider=service-user-provider-facebook&domain=default&username=&password=&ipAddress=&userAgent=", "UTF-8"));
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			HttpEntity<TokenResponse> response = restTemplate.exchange(
				builder.toUriString(), 
				HttpMethod.GET, 
				entity, 
				TokenResponse.class
			);
			log.info("response : "+response);
			
			if (response.hasBody()) {
				accessToken = response.getBody().getAccessToken();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Unable to connect with Facebook " + e);
		}
		return accessToken;
	}
	
	public String getFBGraph(String accessToken) {
		String graph = null;
		try {
			String g = "https://graph.facebook.com/me?" + accessToken;
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(g);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			HttpEntity<String> response = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				entity,
				String.class
			);
			log.info("response : "+response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("ERROR in getting FB graph data. " + e);
		}
		return graph;
	}
	
	public Map<String, String> getGraphData(String fbGraph) {
		Map<String, String> fbProfile = new HashMap<String, String>();
		try {
			JSONObject json = new JSONObject(fbGraph);
			log.info("json : "+json);
			fbProfile.put("id", json.getString("id"));
			fbProfile.put("first_name", json.getString("first_name"));
			if (json.has("email"))
				fbProfile.put("email", json.getString("email"));
			if (json.has("gender"))
				fbProfile.put("gender", json.getString("gender"));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR in parsing FB graph data. " + e);
		}
		return fbProfile;
	}
}
