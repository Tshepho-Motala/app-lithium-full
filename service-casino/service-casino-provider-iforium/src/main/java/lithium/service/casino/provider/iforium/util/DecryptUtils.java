package lithium.service.casino.provider.iforium.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class DecryptUtils {
    private static final int CREDENTIALS_PARTS = 2;

    private final String jasyptEncryptorPassword;

    public DecryptUtils(@Value("${jasypt.encryptor.password}") String jasyptEncryptorPassword) {
        this.jasyptEncryptorPassword = jasyptEncryptorPassword;
    }

    public String decrypt(String value) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(jasyptEncryptorPassword);
        return encryptor.decrypt(value);
    }

    public AuthInfo decodeBasicAuthCredential(String authorization) {
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String[] credentials = new String(credDecoded, StandardCharsets.UTF_8).split(":");

        if (credentials.length != CREDENTIALS_PARTS) {
            return null;
        }

        return new AuthInfo(credentials[0], credentials[1]);
    }

    @Getter
    @AllArgsConstructor
    public static class AuthInfo {
        private final String username;
        private final String password;
    }
}