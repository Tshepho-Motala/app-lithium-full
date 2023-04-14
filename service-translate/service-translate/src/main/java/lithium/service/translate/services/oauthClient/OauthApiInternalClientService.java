package lithium.service.translate.services.oauthClient;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OauthApiInternalClientService {
    @Autowired
    LithiumServiceClientFactory factory;

    private OauthApiInternalClient getClient() throws Status500InternalServerErrorException {
        try {
            return factory.target(OauthApiInternalClient.class, "server-oauth2",false);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500InternalServerErrorException(e.getMessage());//401
        }
    }

    public void validateClientAuth(String authorization) throws Status500InternalServerErrorException {
      getClient().validateClientAuth(authorization);
    }
}
