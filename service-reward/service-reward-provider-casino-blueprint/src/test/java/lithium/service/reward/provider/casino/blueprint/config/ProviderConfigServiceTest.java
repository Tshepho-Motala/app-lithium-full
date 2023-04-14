package lithium.service.reward.provider.casino.blueprint.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.reward.provider.casino.blueprint.utils.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;

@ExtendWith(MockitoExtension.class)
public class ProviderConfigServiceTest {
    @Mock
    private LithiumServiceClientFactory services;

    private ProviderConfigService providerConfigService;

    @Mock
    private ProviderClient providerClient;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void should_get_the_correct_provider_configs() throws Exception {


        Response<Iterable<ProviderProperty>> response = mapper.readValue(fixture(TestConstants.FIXTURE_DOMAIN_PROVIDER_PROPERTIES), new TypeReference<Response<Iterable<ProviderProperty>>>(){});

        Mockito.when(providerClient.findByUrlAndDomainName(Mockito.anyString(), Mockito.anyString()))
                        .thenReturn(Response.<Provider>builder().data(Provider.builder()
                                .url("\"service-reward-provider-casino-blueprint\"")
                                .enabled(true)
                                .build()
                        ).build());

        Mockito.when(providerClient.propertiesByProviderUrlAndDomainName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(response);

        ProviderConfig config = providerConfigService.getConfig("service-reward-provider-casino-blueprint", "livescore_uk");

        Assertions.assertNotNull(config.getBrandId());
        Assertions.assertNotNull(config.getCountryCode());
        Assertions.assertNotNull(config.getJurisdiction());
        Assertions.assertNotNull(config.getPlayerOffset());
        Assertions.assertNotNull(config.getIforiumPlatformKey());
        Assertions.assertNotNull(config.getPlayerGuidPrefix());
        Assertions.assertNotNull(config.getRewardsBaseUrl());
        Assertions.assertNotNull(config.getRewardApiToken());
    }

    @BeforeEach
    public void setup() throws LithiumServiceClientFactoryException {
        providerConfigService = new ProviderConfigService(services);

        Mockito.when(services.target(Mockito.any(),  Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(providerClient);
    }

    @Test
    public void must_error_when_provider_is_not_enabled() {

        Mockito.when(providerClient.findByUrlAndDomainName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Response.<Provider>builder().data(Provider.builder()
                        .url("\"service-reward-provider-casino-blueprint\"")
                        .enabled(false)
                        .build()
                ).build());

        Assertions.assertThrows(Status512ProviderNotConfiguredException.class, () -> providerConfigService.getConfig("service-reward-provider-casino-blueprint", "livescore_uk"));
    }


    @Test
    public void must_error_when_provider_properties_are_null() {

        Mockito.when(providerClient.findByUrlAndDomainName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Response.<Provider>builder().data(Provider.builder()
                        .url("\"service-reward-provider-casino-blueprint\"")
                        .enabled(true)
                        .build()
                ).build());

        Mockito.when(providerClient.propertiesByProviderUrlAndDomainName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Response.<Iterable<ProviderProperty>>builder().build());

        Assertions.assertThrows(Exception.class, () -> providerConfigService.getConfig("service-reward-provider-casino-blueprint", "livescore_uk"));
    }

}
