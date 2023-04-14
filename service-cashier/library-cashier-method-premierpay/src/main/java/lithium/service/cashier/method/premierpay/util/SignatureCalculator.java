package lithium.service.cashier.method.premierpay.util;

import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@NoArgsConstructor
public class SignatureCalculator {
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	
	public String signature(String rcode, String sid, String txId, String status, String descriptor) {
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
			log.warn("Unable to generate signature for: " + sig, e);
		}
		return null;
	}
}
