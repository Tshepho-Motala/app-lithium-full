package lithium.service.cashier.processor.btc.globalbitlocker.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class HashCalculator {
	private LinkedHashMap<String, String> items = new LinkedHashMap<>();
	private String key = null;
	private Long ts = null;
	private String secret;

	public HashCalculator(String secret) {
		this.secret = secret;
	}
	
	public HashCalculator(String key, Long ts, String secret) {
		this.key = key;
		this.ts = ts;
		this.secret = secret;
	}
	
	public HashCalculator addItem(String paramKey, String paramValue) {
		if (paramValue == null) return this;
		if (paramValue.length() == 0) return this;
		items.put(paramKey, paramValue);
		return this;
	}
	
	private String dataToHash(boolean sorted) {
		StringBuffer result = new StringBuffer();
		
		List<String> keys = new ArrayList<>(items.keySet());
		if (sorted) Collections.sort(keys); 
		
		for (String key: keys) {
			if (result.length() > 0) result.append(":");
			result.append(key + ":" + items.get(key));
		}
		if (key != null) {
			if (result.length() > 0) result.append(":");
			result.append("key:" + key);
		}
		if (ts != null) {
			if (result.length() > 0) result.append(":");
			result.append("ts:" + ts);
		}

		return result.toString() + ":" + secret;
	}

	public String calculateHash() {
		return calculateHash(false);
	}

	public String calculateHash(boolean sorted) {
		try {
			String data = dataToHash(sorted);
						
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : md.digest()) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			log.debug("Data in '"+data+"' and hash out '"+sb.toString()+"'");
			
			return sb.toString();
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
