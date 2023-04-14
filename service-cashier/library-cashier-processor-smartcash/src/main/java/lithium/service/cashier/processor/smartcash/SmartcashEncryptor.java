package lithium.service.cashier.processor.smartcash;

import lithium.service.cashier.processor.smartcash.exceptions.SmartcashInvalidSignatureException;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class SmartcashEncryptor {
    private static String DEFAULT_ENCRYPTION_ALGORITHM = "RSA";
    private static String DEFAULT_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    public static PublicKey getPublicKey(String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_ENCRYPTION_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    public static String encryptPin(String data, String publicKey) throws SmartcashInvalidSignatureException {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            log.error("Failed to encryptPin Smartcash pin. Exception: " + e.getMessage(), e);
            throw new SmartcashInvalidSignatureException("Failed to encryptPin Smartcash pin.");
        }
    }

   public static String encryptCallback(String source, String hashKey) throws Exception {
       Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
       SecretKeySpec secretKey = new SecretKeySpec(hashKey.getBytes(), HMAC_SHA256_ALGORITHM);
       mac.init(secretKey);
       return Base64.getEncoder().encodeToString(mac.doFinal(source.getBytes()));
   }

}
