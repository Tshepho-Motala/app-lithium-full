package lithium.service.casino.provider.twowinpower.util;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashCalculator {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private Vector<String> items;
	private String password;
	
	public HashCalculator(String password) {
		items = new Vector<String>();
		this.password = password;
	}
	
	public void addItem(String item) {
		if (item != null) items.add(item);
	}
	
	public void addItem(Boolean item) {
		if (item != null) items.add(item.toString());
	}
	
	public void addItem(Integer item) {
		if (item != null) items.add(item.toString());
	}

	public void addItem(Long item) {
		if (item != null) items.add(item.toString());
	}	
	
	public void addItem(int item) {
		items.add(new Integer(item).toString());
	}

	public String calculateMd5() {
		String allItems = "";
		for (String item: items) {
			allItems += item;
		}
		
		allItems += password;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(allItems.getBytes());
			
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String calculateSha1(String urlEncoded) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			
			Key key = new SecretKeySpec(password.getBytes(), HMAC_SHA1_ALGORITHM);
			mac.init(key);
			
			byte[] output = mac.doFinal(urlEncoded.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : output) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString().toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
