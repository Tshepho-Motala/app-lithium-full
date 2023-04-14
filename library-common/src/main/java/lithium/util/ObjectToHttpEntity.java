package lithium.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ObjectToHttpEntity {
	public static HttpEntity<MultiValueMap<String, String>> forPostForm(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> mvmap = new LinkedMultiValueMap<String, String>();
		Map<String, String> map = ObjectToStringMap.toStringMap(o);
		for (String key: map.keySet()) {
			mvmap.add(key, map.get(key));
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(mvmap, headers);
		return request;
	}
	public static HttpEntity<MultiValueMap<String, String>> forPostFormFormParam(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> mvmap = new LinkedMultiValueMap<String, String>();
		Map<String, String> map = ObjectToStringMap.toStringMapFormMap(o);
		for (String key: map.keySet()) {
			mvmap.add(key, map.get(key));
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(mvmap, headers);
		return request;
	}
}
