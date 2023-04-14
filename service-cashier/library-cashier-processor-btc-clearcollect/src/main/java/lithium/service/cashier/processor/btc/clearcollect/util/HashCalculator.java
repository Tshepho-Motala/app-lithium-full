package lithium.service.cashier.processor.btc.clearcollect.util;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashCalculator {
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA384";
	private String password;
	
	public HashCalculator(String password) {
		this.password = password;
	}
	
	public String calculateHash(String payload) {
		
		
		
		try {
			
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			
			Key key = new SecretKeySpec(password.getBytes(), HMAC_SHA1_ALGORITHM);
			mac.init(key);
			byte[] output = mac.doFinal(payload.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : output) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
