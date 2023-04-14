package lithium.service.casino.provider.slotapi.services.oauthClient;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class OauthApiInternalClientService {
    @Autowired
    LithiumServiceClientFactory factory;

    private OauthApiInternalClient getClient() throws Status500InternalServerErrorException {
        try {
            return factory.target(OauthApiInternalClient.class, "server-oauth2",false);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500InternalServerErrorException(e.getMessage());
        }
    }

    public void validateClientAuth(String authorization) throws Status500InternalServerErrorException {
      getClient().validateClientAuth(authorization);
    }

    public String getClientId(String authorization) {
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        return values[0];
    }
}
