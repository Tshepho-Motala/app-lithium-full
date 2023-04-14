package lithium.service.casino.provider.evolution;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceCasinoProviderEvolutionModuleInfo extends ModuleInfoAdapter {

    public ServiceCasinoProviderEvolutionModuleInfo() {
        ProviderConfig providerConfig = ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.CASINO)
                .properties(getProviderProperties())
                .build();

        addProvider(providerConfig);
    }

    private List<ProviderConfigProperty> getProviderProperties() {
        List<ProviderConfigProperty> properties = new ArrayList<>();

        return properties;
    }

}
