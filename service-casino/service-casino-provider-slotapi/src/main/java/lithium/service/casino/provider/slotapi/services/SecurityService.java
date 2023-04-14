package lithium.service.casino.provider.slotapi.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.config.ProviderConfig;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.services.oauthClient.OauthApiInternalClientService;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityService {

    @Autowired @Setter ProviderConfigService configService;
    @Autowired @Setter ModuleInfo moduleInfo;
    @Autowired OauthApiInternalClientService oauthApiInternalClientService;

    public void validateSha256(String domainName, String[] items, String sha256)
            throws Status470HashInvalidException, Status500ProviderNotConfiguredException {
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);
        String preSharedKey = config.getHashPassword();

        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
        for (int i = 0; i < items.length; i++) {
            hasher.addItem(items[i]);
        }
        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(sha256)) {
            log.warn("Expected " + expectedHash + " sha256 but got " + sha256);
            throw new Status470HashInvalidException();
        }
    }

    public void validateBasicAuth(String authorization) throws Status401UnAuthorisedException {
        try {
            oauthApiInternalClientService.validateClientAuth(authorization);
        } catch (Exception e) {
            log.error("Invalid Basic Token used in Authorization Header");
            throw new Status401UnAuthorisedException("Invalid Basic Token used in Authorization Header");
        }
    }
}