package lithium.util;

import lithium.exceptions.Status500InternalServerErrorException;
import org.junit.Assert;
import org.junit.Test;

public class PasswordHashingTest {
    @Test
    public void testHashPasswordUsingSha1Salted() throws Status500InternalServerErrorException {
        String password = "Qw123456";
        Hash.Type hashAlgorithm = Hash.Type.SHA1_ALGORITHM;
        String salt = "OEAGyrst";
        HashSaltPosition saltPosition = HashSaltPosition.POST;
        String hashPassword = PasswordHashing.hashPassword(password, hashAlgorithm, salt, saltPosition);
        Assert.assertEquals("7E4BCD2B025A7A040FD12D69AFE8A5414D12ECA4", hashPassword);
    }

    @Test
    public void testHashPasswordUsingPbkdf2() throws Status500InternalServerErrorException {
        String password = "Password0";
        Hash.Type hashAlgorithm = Hash.Type.PBKDF2_ALGORITHM;
        String salt = "03CxkIcYDUkVcmsNJ/+EMrMq86zajfRPSJIU04/GYUI=";
        HashSaltPosition saltPosition = HashSaltPosition.POST;
        String hashPassword = PasswordHashing.hashPassword(password, hashAlgorithm, salt, saltPosition);
        Assert.assertEquals("9UUgnv1lJiuLhUXF9Plo4EtlVS4=", hashPassword);
    }
}
