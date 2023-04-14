package lithium.service.kyc.provider.smileindentity.service.util;

import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class Signature {
    private Integer partnerId;
    private String apiKey;

    public Signature(String partnerIdString, String apiKey) {
        int partnerId = Integer.parseInt(partnerIdString);
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }

    public String generateSecKey(Long timestamp) throws Status515SignatureCalculationException {
        String toHash = partnerId + ":" + timestamp;
        String signature = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(toHash.getBytes());
            byte[] hashed = md.digest();
            String hashSignature = bytesToHexStr(hashed);
            PublicKey publicKey = loadPublicKey(apiKey);
            byte[] encSignature = encryptString(publicKey, hashSignature);
            signature = Base64.getEncoder().encodeToString(encSignature) + "|" + hashSignature;
        } catch (Exception e) {
            log.warn("Can't calculate signature: " + toHash + ". Stacktrace: "+String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new Status515SignatureCalculationException();
        }
        return signature;
    }

    private static PublicKey loadPublicKey(String apiKey) throws GeneralSecurityException, IOException {
        byte[] data = Base64.getDecoder().decode((apiKey.getBytes()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factObj = KeyFactory.getInstance("RSA");
        PublicKey lPKey = factObj.generatePublic(spec);
        return lPKey;
    }

    private static byte[] encryptString(PublicKey key, String plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes());
    }

    private static String bytesToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
