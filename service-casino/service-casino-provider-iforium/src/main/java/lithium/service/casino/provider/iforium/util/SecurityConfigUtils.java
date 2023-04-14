package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.config.IforiumProviderConfig;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityConfigUtils {

    private final ProviderConfigService providerConfigService;
    private final DecryptUtils decryptUtils;

    @SneakyThrows
    private void validateAuthorization(String ipAddress, IforiumProviderConfig iforiumProviderConfig) {
        if (!iforiumProviderConfig.getWhitelistIPs().contains(ipAddress)) {
            String errorMessage = format("RemoteAddress=%s is not whitelisted.", ipAddress);
            log.warn(errorMessage);
            throw new AuthorizationServiceException(errorMessage);
        }
    }

    @SneakyThrows
    private void validateAuthentication(String authorization, IforiumProviderConfig iforiumProviderConfig) {
        DecryptUtils.AuthInfo authInfo = decryptUtils.decodeBasicAuthCredential(authorization);

        if (authInfo == null || !iforiumProviderConfig.getSecureUserName().equals(authInfo.getUsername())
                || !iforiumProviderConfig.getSecureUserPassword().equals(authInfo.getPassword())) {
            String errorMessage = format("Bad credentials. User=%s is not authorized", iforiumProviderConfig.getSecureUserName());
            log.error(errorMessage);
            throw new AuthenticationServiceException(errorMessage);
        }
    }

    public void validateSecurity(String authorization, String ipAddress, String domainName) throws Status512ProviderNotConfiguredException {
        IforiumProviderConfig iforiumProviderConfig = providerConfigService.getIforiumConfig(domainName);

        validateAuthorization(ipAddress, iforiumProviderConfig);
        validateAuthentication(authorization, iforiumProviderConfig);
    }
}