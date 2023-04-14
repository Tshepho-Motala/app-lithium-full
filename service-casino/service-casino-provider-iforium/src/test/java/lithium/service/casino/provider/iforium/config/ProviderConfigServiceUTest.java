package lithium.service.casino.provider.iforium.config;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.IforiumModuleInfo;
import lithium.service.casino.provider.iforium.util.DecryptUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.JASYPT_ENCRYPTOR_PASSWORD;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SECURE_PASSWORD;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SECURE_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({MockitoExtension.class})
class ProviderConfigServiceUTest {

    ProviderConfigService providerConfigService;

    DecryptUtils decryptUtils;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Mock
    private ProviderClient providerClient;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        decryptUtils = new DecryptUtils(JASYPT_ENCRYPTOR_PASSWORD);
        providerConfigService = new ProviderConfigService(lithiumServiceClientFactory, decryptUtils, new IforiumModuleInfo());
    }

    @Test
    void failureGetProviderClient() {
        mockFailureProviderClient();

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenDomainStatusIsNotFound() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.NOT_FOUND, Provider.builder().enabled(true).build());

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenDomainIsDisabled() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.OK, Provider.builder().enabled(false).build());

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenDomainDataIsNUll() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.OK, null);

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenProviderPropertiesIsNotFound() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();
        mockFailurePropertiesByProviderUrlAndDomainName(Response.Status.NOT_FOUND, Collections.emptyList());

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenProviderPropertiesIsNUll() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();
        mockFailurePropertiesByProviderUrlAndDomainName(Response.Status.OK, null);

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class,
                                                                         () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("The provider is not configured for this domain: domain"));
    }

    @Test
    void failureWhenPropertyIsNotFoundInBOConfigurtion() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();

        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value("10.10.10.10").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName()).value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        mockSuccessPropertiesByProviderUrlAndDomainName(providerProperties);

        Exception exception = assertThrows(Exception.class, () -> providerConfigService.getIforiumConfig(DOMAIN_NAME));

        assertTrue(exception.getMessage().contains("Property=secureUserPassword is not configured in BO"));
    }

    @Test
    @SneakyThrows
    void success() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();
        mockSuccessPropertiesByProviderUrlAndDomainName(validProviderProperties());

        IforiumProviderConfig iforiumProviderConfig = providerConfigService.getIforiumConfig(DOMAIN_NAME);

        assertEquals(SECURE_USERNAME, iforiumProviderConfig.getSecureUserName());
        assertEquals(SECURE_PASSWORD, iforiumProviderConfig.getSecureUserPassword());
        assertEquals(Collections.singletonList("10.10.10.10"), iforiumProviderConfig.getWhitelistIPs());
    }

    @SneakyThrows
    private void mockSuccessProviderClient() {
        Mockito.when(lithiumServiceClientFactory.target(ProviderClient.class, "service-domain", true)).thenReturn(providerClient);
    }

    @SneakyThrows
    private void mockFailureProviderClient() {
        Mockito.when(lithiumServiceClientFactory.target(ProviderClient.class, "service-domain", true))
               .thenThrow(LithiumServiceClientFactoryException.class);
    }

    @SneakyThrows
    private void mockSuccessFindByUrlAndDomainName() {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(Provider.builder().enabled(true).build()).build();
        Mockito.doReturn(response).when(providerClient).findByUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    private void mockFailureFindByUrlAndDomainName(Response.Status status, Provider provider) {
        Response<Object> response = Response.builder().status(status).data(provider).build();
        Mockito.doReturn(response).when(providerClient).findByUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    private void mockSuccessPropertiesByProviderUrlAndDomainName(List<ProviderProperty> providerProperties) {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(providerProperties).build();
        Mockito.doReturn(response).when(providerClient).propertiesByProviderUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    private void mockFailurePropertiesByProviderUrlAndDomainName(Response.Status status, List<ProviderProperty> providerProperties) {
        Response<Object> response = Response.builder().status(status).data(providerProperties).build();
        Mockito.doReturn(response).when(providerClient).propertiesByProviderUrlAndDomainName(any(), any());
    }

    private List<ProviderProperty> validProviderProperties() {
        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value("10.10.10.10").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName())
                                               .value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_PASSWORD.getName())
                                               .value("9I+zKGlx3CHQ4DC+TkSR3eeRJBtwrLM0").build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.LOBBY_URL.getName()).value("https://www.operator.com/lobby")
                                .build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.STARTGAME_BASE_URL.getName()).value("google.com").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.CASINO_ID.getName()).value("S0009").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.LIST_GAME_URL.getName()).value("URL").build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.REGULATIONS_ENABLED.getName()).value("true").build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_OVERRIDE_RTS_13_MODE.getName()).value("disabled")
                                .build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_OVERRIDE_CMA_MODE.getName()).value("disabled").build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_SESSION_DURATION.getName()).value("0").build());
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_INTERVAL.getName()).value("86400").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_GAME_HISTORY_URL.getName())
                                               .value("https://www.operator.com/history").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_BONUS_URL.getName())
                                               .value("https://www.operator.com/bonus").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL.getName())
                .value("https://sapirgsuat.blueprintgaming.com/iforium/SAPI.asmx/Progressive").build());
        return providerProperties;
    }

}
