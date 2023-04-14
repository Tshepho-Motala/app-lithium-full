package lithium.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Optional;

/**
 * <p>Class used for hashing. This has been duplicated in many places, adding it here so everyone can make use of it.</p>
 * <p>This class is able to handle any of the following algorithms:
 *  <ul>
 *     <li>MD5</li>
 *     <li>SHA-1</li>
 *     <li>256</li>
 *     <li>SHA-512</li>
 *     <li>HmacSHA1</li>
 *     <li>HmacSHA256</li>
 *     <li>HmacSHA512</li>
 *  </ul>
 * </p>
 * <p>Example Usage: </p>
 * <blockquote><pre>
 *      String requestData = "Something I want to hash with md5";
 *      String md5Data = Hash.builderMd5(requestData).md5();
 *      String delim = ":";
 *      String md5Data = Hash.builderMd5(delim, requestData, "value2", "value3").md5();
 *      <hr/>
 *      String secret = "supersafesecret";
 *      String stringToHash = "Something else that needs some hashing";
 *      String signature = Hash.builder(secret, stringToHash).sha512();
 *      <hr/>
 *      String secret = "supersafesecret2";
 *      String delim = ":";
 *      String signature = Hash.builder(secret, delim, "value1", "value2", "value3").hmacSha256();
 * </pre></blockquote>
 * @author Riaan
 */

@Slf4j
public class Hash {
	private static final String DEFAULT_ENCODING = "UTF-8";

	private String key = "";
	private String value = "";

	private Hash(String key, String value) throws Exception {
		log.debug("key: "+key+" value: "+value);
		if (key == null) throw new Exception("Key required.");
		if ((value == null) || (value.isEmpty())) throw new Exception("Value required.");
		this.key = key;
		this.value = value;
	}

	public Hash(String value) throws Exception {
		log.debug("value: "+value);
		if ((value == null) || (value.isEmpty())) throw new Exception("Value required.");
		this.value = value;
	}

	public static HashBuilder builder(String key, String delimiter, String... values) {
		return new HashBuilder(key, delimiter, values);
	}
	public static HashBuilder builder(String key, String delimiter, Boolean... values) {
		return new HashBuilder(key, delimiter, values);
	}
	public static HashBuilder builder(String key, String delimiter, Integer... values) {
		return new HashBuilder(key, delimiter, values);
	}
	public static HashBuilder builder(String key, String delimiter, Long... values) {
		return new HashBuilder(key, delimiter, values);
	}
	public static HashBuilder builder(String value) {
		return new HashBuilder(value);
	}
	public static HashBuilder builder(String key, String value) {
		return new HashBuilder(key, value);
	}
	public static HashBuilder builder(String value, String salt, HashSaltPosition saltPosition) {
		return new HashBuilder(value, salt, saltPosition);
	}
	public static HashBuilder builderSha512(String value) {
		return new HashBuilder(value);
	}
	public static HashBuilder builderMd5(String value) {
		return new HashBuilder(value);
	}
	public static HashBuilder builderMd5(String delimiter, String... values) {
		return new HashBuilder(delimiter, values);
	}

	public static class HashBuilder {
		private String key = "";
		private String value = "";

		public HashBuilder(String key, String delimiter, Boolean... values) {
			if (delimiter == null) delimiter = ":";
			this.key = key;
			for (Boolean value: values) {
				this.value += value + delimiter;
			}
			if (!delimiter.isEmpty()) this.value = removeLastCharacter(this.value);
		}
		public HashBuilder(String key, String delimiter, Integer... values) {
			if (delimiter == null) delimiter = ":";
			this.key = key;
			for (Integer value: values) {
				this.value += value + delimiter;
			}
			if (!delimiter.isEmpty()) this.value = removeLastCharacter(this.value);
		}
		public HashBuilder(String key, String delimiter, Long... values) {
			if (delimiter == null) delimiter = ":";
			this.key = key;
			for (Long value: values) {
				this.value += value + delimiter;
			}
			if (!delimiter.isEmpty()) this.value = removeLastCharacter(this.value);
		}
		public HashBuilder(String key, String delimiter, String... values) {
			if (delimiter == null) delimiter = ":";
			this.key = key;
			for (String value: values) {
				this.value += value + delimiter;
			}
			if (!delimiter.isEmpty()) this.value = removeLastCharacter(this.value);
		}
		public HashBuilder(String key, String value) {
			this.key = key;
			this.value = value;
		}
		public HashBuilder(String value) {
			this.value = value;
		}
		public HashBuilder(String delimiter, String... values) {
			if (delimiter == null) delimiter = ":";
			for (String value: values) {
				this.value += value + delimiter;
			}
			if (!delimiter.isEmpty()) this.value = removeLastCharacter(this.value);
		}
		public HashBuilder(String value, String salt, HashSaltPosition saltPosition) {
			this.value = switch (saltPosition) {
				case PRE -> salt + value;
				case POST -> value + salt;
			};
		}

		private String removeLastCharacter(String str) {
			String result = Optional.ofNullable(str)
				.filter(sStr -> sStr.length() != 0)
				.map(sStr -> sStr.substring(0, sStr.length() - 1))
				.orElse(str);
			return result;
		}

		public Hash build() throws Exception {
			return new Hash(key, value);
		}

		public String hmacSha1() throws Exception {
			return new Hash(key, value).hmacSha1();
		}
		public String hmacSha256() throws Exception {
			return new Hash(key, value).hmacSha256();
		}
		public String hmacSha512() throws Exception {
			return new Hash(key, value).hmacSha512();
		}
		public String sha1() throws Exception {
			return new Hash(value).sha1();
		}
		public String sha256() throws Exception {
			return new Hash(key, value).sha256();
		}
		public String sha512() throws Exception {
			return new Hash(key, value).sha512();
		}
		public String md5() throws Exception {
			return new Hash(value).md5();
		}
		public String sha256WithRSA() throws Exception {
			return new Hash(key, value).calculateRSA(Type.SHA256_RSA_ALGORITHM);
		}
		public String pbkdf2(String salt, int iterations, int keyLength) throws Exception {
			return new Hash(value).pbkdf2(salt, iterations, keyLength);
		}
	}

	public String hmacSha1() {
		return calculateHmac(Type.HMAC_SHA1_ALGORITHM);
	}

	public String hmacSha256() {
		return calculateHmac(Type.HMAC_SHA256_ALGORITHM);
	}

	public String hmacSha512() {
		return calculateHmac(Type.HMAC_SHA512_ALGORITHM);
	}

	public String sha1() {
		return sha(Type.SHA1_ALGORITHM);
	}

	public String sha256() {
		return sha(Type.SHA256_ALGORITHM);
	}

	public String sha512() {
		return sha(Type.SHA512_ALGORITHM);
	}

	private String sha(Type type) {
		StringBuffer hexString = new StringBuffer();
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(type.algorithm());
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return hexString.toString();
	}

	private String calculateHmac(Type type) {
		try {
			Mac mac = Mac.getInstance(type.algorithm());

			Key macKey = new SecretKeySpec(key.getBytes(), type.algorithm());
			mac.init(macKey);

			byte[] output = mac.doFinal(value.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : output) {
				sb.append(String.format("%02x", b & 0xff));
			}

			return sb.toString().toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private String calculateRSA(Type type) {
		try {
			byte[] keyBytes =  Base64.decodeBase64(key.getBytes(DEFAULT_ENCODING));
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

			Signature signature = Signature.getInstance(type.algorithm);
			signature.initSign(privateKey);
			signature.update(value.getBytes(DEFAULT_ENCODING));

			byte[] signatureBytes = Base64.encodeBase64(signature.sign());
			return new String(signatureBytes, DEFAULT_ENCODING);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String md5() {
		try {
			MessageDigest md = MessageDigest.getInstance(Type.MD5_ALGORITHM.algorithm());
			md.update(value.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : md.digest()) {
				sb.append(String.format("%02x", b & 0xff));
			}

			log.debug("Data in '"+value+"' and hash out '"+sb.toString()+"'");

			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String pbkdf2(String salt, int iterations, int keyLength) {
		try {
			byte[] saltBytes = java.util.Base64.getDecoder().decode(salt);
			PBEKeySpec spec = new PBEKeySpec(this.value.toCharArray(), saltBytes, iterations, keyLength);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			SecretKey key = factory.generateSecret(spec);
			String base64hash = new String(java.util.Base64.getEncoder().encode(key.getEncoded()));
			return base64hash;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@ToString
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@AllArgsConstructor(access= AccessLevel.PRIVATE)
	public enum Type {
		MD5_ALGORITHM("MD5"),
		SHA1_ALGORITHM("SHA-1"),
		SHA256_ALGORITHM("SHA-256"),
		SHA256_RSA_ALGORITHM("SHA256withRSA"),
		SHA512_ALGORITHM("SHA-512"),
		HMAC_SHA1_ALGORITHM("HmacSHA1"),
		HMAC_SHA256_ALGORITHM("HmacSHA256"),
		HMAC_SHA512_ALGORITHM("HmacSHA512"),
		PBKDF2_ALGORITHM("PBKDF2");

		@Getter
		@Setter
		@Accessors(fluent = true)
		private String algorithm;

		@JsonCreator
		public static Hash.Type fromAlgorithm(String algorithm) {
			for (Hash.Type c : Hash.Type.values()) {
				if (c.algorithm == algorithm) {
					return c;
				}
			}
			return null;
		}
	}
}