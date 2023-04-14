package lithium.service.kyc.provider.onfido.service;

import com.onfido.Onfido;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.modules.ModuleInfo;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.onfido.config.ProviderConfig;
import lithium.service.kyc.provider.onfido.config.ProviderConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class OnfidoBaseService {
    @Autowired
    private ProviderConfigService configService;
    @Autowired
    private ModuleInfo moduleInfo;
    public Onfido getOnfidoClient(String domainName) throws Status500InternalServerErrorException {
        ProviderConfig config = getConfig(domainName);
        return getOnfidoClient(config);
    }
    public Onfido getOnfidoClient(ProviderConfig config) {
        return Onfido.builder()
                .apiToken(config.getApiToken())
                .unknownApiUrl(config.getBaseUrl())
                // Supports .regionEU, .regionUS() and .regionCA()
                .build();
    }

    protected ProviderConfig getConfig(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        return configService.getConfig(moduleInfo.getModuleName(), domainName);
    }
}
