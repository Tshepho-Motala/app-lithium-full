package lithium.service.translate.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.service.translate.services.oauthClient.OauthApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityService {
    @Autowired OauthApiInternalClientService oauthApiInternalClientService;

    public void validateBasicAuth(String authorization) throws Status401UnAuthorisedException {
        try {
            oauthApiInternalClientService.validateClientAuth(authorization);
        } catch (Exception e) {
            log.error("Invalid Basic Token used in Authorization Header");
            throw new Status401UnAuthorisedException("Invalid Basic Token used in Authorization Header");
        }
    }
}
