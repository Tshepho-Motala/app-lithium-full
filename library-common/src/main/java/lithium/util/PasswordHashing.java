package lithium.util;

import com.google.common.hash.Hashing;
import lithium.exceptions.Status500InternalServerErrorException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class PasswordHashing {

    public static String hashPassword(String password, String passwordSalt) throws Status500InternalServerErrorException {
        try {
            String hashed;
            if (StringUtil.isEmpty(passwordSalt)) {
                hashed = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            } else if (skipHashing(password)) {
                hashed = decodePasswordToSkipHashing(password);
            } else {
                hashed = Hash.builder(passwordSalt, password).hmacSha256();
                hashed = "st:" + hashed;
            }

            log.debug("hashPassword " + password + " " + hashed);
            return hashed;
        } catch (Exception e) {
            throw new Status500InternalServerErrorException("service-user", ExceptionMessageUtil.allMessages(e), e);
        }
    }

    public static String hashPassword(String password, Hash.Type hashAlgorithm, String salt,
            HashSaltPosition saltPosition) throws Status500InternalServerErrorException {
        try {
            return switch (hashAlgorithm) {
                case SHA1_ALGORITHM -> Hash.builder(password, salt, saltPosition).sha1().toUpperCase(); // DK sha1 password hashes are uppercase
                case PBKDF2_ALGORITHM -> Hash.builder(password).pbkdf2(salt, 16000, 160); // pbkdf2 no uppercase
                default -> throw new RuntimeException(hashAlgorithm.algorithm() + " is unsupported");
            };
        } catch (Exception e) {
            throw new Status500InternalServerErrorException("service-user", ExceptionMessageUtil.allMessages(e), e);
        }
    }

    public static String encodePasswordToSkipHashing(String passwordHash) {
        return "skip:" + hashPassword(passwordHash) + ":::" + passwordHash;
    }

    @SneakyThrows
    private static String hashPassword(String password) {
        return Hash.builder(password).build().sha1();
    }

    private static boolean skipHashing(String password) {
        if (decodePasswordToSkipHashing(password) != null) {
            return true;
        }
        return false;
    }

    private static String decodePasswordToSkipHashing(String password) {
        if (password.startsWith("skip:")) {
            String[] el = password.split(":::");
            if (el.length == 2) {
                if (el[0].replaceFirst("skip:", "").contentEquals(hashPassword(el[1]))) {
                    return el[1];
                }
            }
        }
        return null;
    }
}
