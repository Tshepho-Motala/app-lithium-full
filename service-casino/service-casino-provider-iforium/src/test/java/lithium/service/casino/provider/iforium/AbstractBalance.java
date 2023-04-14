package lithium.service.casino.provider.iforium;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.model.request.BalanceRequest;
import lithium.service.casino.provider.iforium.util.LithiumClientUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;

public class AbstractBalance {

    protected CachingDomainClientService cachingDomainClientService;

    protected CasinoClientService casinoClientService;

    protected LithiumServiceClientFactory lithiumServiceClientFactory;

    protected LithiumClientUtils lithiumClientUtils;

    @SneakyThrows
    protected void mockExternalServices() {
        casinoClientService = Mockito.mock(CasinoClientService.class);
        cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
        lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);
        lithiumClientUtils = new LithiumClientUtils(lithiumServiceClientFactory);
    }

    protected lithium.service.casino.client.objects.response.BalanceResponse validCasinoClientBalanceResponse(String balanceCents) {
        return lithium.service.casino.client.objects.response.BalanceResponse.builder().balanceCents(Long.valueOf(balanceCents)).build();
    }

    public static Domain validDomain() {
        return Domain.builder().name(TestConstants.DOMAIN_NAME).currency(TestConstants.GBP_CURRENCY).build();
    }

    @SneakyThrows
    public void mockSuccessGetLastLoginEvent() {
        SystemLoginEventsClient systemLoginEventsClient = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.doReturn(systemLoginEventsClient).when(lithiumServiceClientFactory).target(SystemLoginEventsClient.class, "service-user", true);
        Mockito.when(systemLoginEventsClient.getLastLoginEventForUser(anyString())).thenReturn(LoginEvent.builder().build());
    }

    @SneakyThrows
    public void mockFailureGetLastLoginEvent() {
        SystemLoginEventsClient systemLoginEventsClient = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.doReturn(systemLoginEventsClient).when(lithiumServiceClientFactory).target(SystemLoginEventsClient.class, "service-user", true);
        Mockito.when(systemLoginEventsClient.getLastLoginEventForUser(anyString())).thenThrow(new Status411UserNotFoundException("user is invalid"));
    }

    @SneakyThrows
    public static String buildBalanceRequest(String platformKey, String sequence, Date timestamp,
                                             String operatorAccountId, String gameId, String contentGameProviderId) {
        return new ObjectMapper().writeValueAsString(BalanceRequest.builder()
                                                                   .platformKey(platformKey)
                                                                   .sequence(sequence)
                                                                   .timestamp(timestamp)
                                                                   .operatorAccountId(operatorAccountId)
                                                                   .gameId(gameId)
                                                                   .contentGameProviderId(contentGameProviderId)
                                                                   .build()
        );
    }
}
