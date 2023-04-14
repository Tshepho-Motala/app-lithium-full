package lithium.service.cashier.mock.neteller.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MockService {
//	@Autowired
//	@Qualifier("lithium.service.user.mock.vipps.resttemplate")
//	private RestTemplate restTemplate;
	
	@Value("accessTokenClientSecret") private String accessTokenClientSecret;
	
	public String createJWT() {
		String id = UUID.randomUUID().toString();
		String issuer = "service-user-mock-neteller";
		String subject = "service-user-provider-neteller";
		long ttlMillis = 24 * 60 * 60 * 1000;
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(Base64.getEncoder().encodeToString(accessTokenClientSecret.getBytes()));
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
		long expMillis = nowMillis + ttlMillis;
		
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
		.setId(id)
		.setIssuedAt(now)
		.setSubject(subject)
		.setIssuer(issuer)
		.setExpiration(new Date(expMillis))
		.signWith(signatureAlgorithm, signingKey);
		
		// Builds the JWT and serializes it to a compact, URL-safe string
		log.info("createJWT :: "+builder.compact());
		return builder.compact();
	}
}