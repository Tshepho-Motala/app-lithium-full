package services;

import lithium.client.changelog.ChangeLogService;
import lithium.modules.ModuleInfo;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.data.ExternalBonusRestrictionRequest;
import lithium.service.casino.provider.sportsbook.response.BonusRestrictionResponse;
import lithium.service.casino.provider.sportsbook.services.BonusRestrictionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BonusRestrictionServiceTest {

    @Mock
    Logger log;

    @Mock
    ModuleInfo moduleInfo;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ProviderConfigService providerConfigService;

    @Mock
    ChangeLogService changeLogService;

    @InjectMocks
    BonusRestrictionService bonusRestrictionService;

    BonusRestrictionRequest request;


    @Before
    public void setUp() throws Status512ProviderNotConfiguredException {
        ProviderConfig config = new ProviderConfig();
        config.setBonusRestrictionKey("sportsbook_hash_key");
        config.setBonusRestrictionUrl("https://jsonplaceholder.typicode.com/todos");

        Mockito.when(moduleInfo.getModuleName()).thenReturn("service-casino-provider-sportsbook");
        Mockito.when(providerConfigService.getConfig(Mockito.anyString(), Mockito.anyString())).thenReturn(config);


         request = BonusRestrictionRequest.builder()
                .playerGuid("livescore_uk/rivalani06")
                .restricted(true)
                .playerId(2021)
                .build();
    }

    @Test
    public void shouldReturnAProperResponseWhenItIsSuccessful() throws Exception {

        BonusRestrictionResponse bonusRestrictionResponse = BonusRestrictionResponse.builder().errorCode(0).errorMessage("Success").build();

        ResponseEntity<BonusRestrictionResponse> response = ResponseEntity.status(200).body(bonusRestrictionResponse);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(ExternalBonusRestrictionRequest.class), Mockito.any(Class.class)))
                .thenReturn(response);

        BonusRestrictionResponse localResponse = bonusRestrictionService.toggle(request, "livescore_uk");

        Mockito.verify(log, Mockito.times(1)).debug(Mockito.anyString(), Mockito.any(Object.class));

        assertTrue(localResponse.getErrorCode() == 0);
        assertEquals(localResponse.getErrorMessage(), "Success");
    }

    @Test
    public void shouldLogToErrorAndDebugWhenRequestFails() throws Exception {

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(ExternalBonusRestrictionRequest.class), Mockito.any(Class.class)))
                .thenThrow(HttpServerErrorException.class);

        bonusRestrictionService.toggle(request, "livescore_uk");

        Mockito.verify(log, Mockito.times(1)).debug(Mockito.anyString());
        Mockito.verify(log, Mockito.times(1)).error(Mockito.anyString(), Mockito.any(Exception.class));
    }
}
