package lithium.service.cashier.processor.neosurf.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

 
public class HashCalculator {
	private LinkedHashMap<String, String> items = new LinkedHashMap<>();
	private String key = null;

	public HashCalculator(String key) {
		this.key = key;
	}

	public HashCalculator addItem(String paramKey, String paramValue) {
		if (paramValue == null)
			return this;
		if (paramValue.length() == 0)
			return this;
		items.put(paramKey, paramValue);
		return this;
	}

	private String dataToHash() {
		StringBuffer result = new StringBuffer();

		List<String> keys = new ArrayList<>(items.keySet());

		for (String key : keys) {
			result.append(items.get(key));
		}
		if (key != null) {
			result.append(key);
		}
 		return result.toString();
	}

	public String calculateHash() {
		try {
 			String data = dataToHash();

			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

 
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
