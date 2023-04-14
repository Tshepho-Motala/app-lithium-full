package lithium.service.cashier.processor.flexepin.util;

import lombok.extern.slf4j.Slf4j;

import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class HashMacCalculator {
private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
private static final String HMAC_ENCODING="ASCII";
	
	public String signature(String rcode, String sid, String txId, String status, String descriptor) throws Exception {
		String sig = sid+"|"+txId+"|"+status+"|"+descriptor;
		try {
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		
			Key key = new SecretKeySpec(rcode.getBytes(), HMAC_SHA256_ALGORITHM);
			mac.init(key);
			byte[] output = mac.doFinal(sig.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : output) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		} catch (Exception e) {
	    	 throw new Exception("error occcured calculating hex64sha256 string",e);
		}
 	}
	public static String hex64sha256(String data, String secret) throws Exception {
	    String hash = null;
	    try {
	        Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256_ALGORITHM);
	        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(HMAC_ENCODING), HMAC_SHA256_ALGORITHM);
	        sha256_HMAC.init(secret_key);
	        byte[] res = sha256_HMAC.doFinal(data.getBytes(HMAC_ENCODING));
	        hash = bytesToHex(res);
 	    } catch (Exception e){
 	    	 throw new Exception("error occcured calculating hex64sha256 string",e);
 	    }
	    return hash;
	}

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
