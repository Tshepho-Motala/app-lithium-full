package lithium.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class SecurityKeyPairGenerator {
	public static final String KEYPAIR_ALGORITHM = "RSA";
	public static final int KEYPAIR_KEY_SIZE = 2048;
	static private Base64.Encoder encoder = Base64.getEncoder();

	static public void writeBase64(Writer out, Key key)
			throws java.io.IOException {
		byte[] buf = key.getEncoded();
		out.write(encoder.encodeToString(buf));
		out.write("\n");
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEYPAIR_ALGORITHM);
		kpg.initialize(KEYPAIR_KEY_SIZE);
		KeyPair kp = kpg.generateKeyPair();

		return kp;
//
//		StringWriter publicKey = new StringWriter();
//		StringWriter privateKey = new StringWriter();
//
//		writeBase64(privateKey, kp.getPrivate());
//		writeBase64(publicKey, kp.getPublic());
//
//		log.error("Public key: " + publicKey.toString());
//		log.error("Private key: " + privateKey.toString());
	}

	public String printPrivateKeyInPemFormat(KeyPair kp) {
		StringBuilder sb = new StringBuilder();
		sb.append("-----BEGIN PRIVATE KEY-----");
		sb.append("\n");
		sb.append(Base64.getMimeEncoder().encodeToString( kp.getPrivate().getEncoded()));
		sb.append("\n");
		sb.append("-----END PRIVATE KEY-----");
		sb.append("\n");

		return sb.toString();
	}

	public String printPublicKeyInPemFormat(KeyPair kp) {
		StringBuilder sb = new StringBuilder();
		sb.append("-----BEGIN PUBLIC KEY-----");
		sb.append("\n");
		sb.append(Base64.getMimeEncoder().encodeToString( kp.getPublic().getEncoded()));
		sb.append("\n");
		sb.append("-----END PUBLIC KEY-----");
		sb.append("\n");

		return sb.toString();
	}
}
